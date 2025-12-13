package com.vaultogether.vaultogetherbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaultogether.vaultogetherbackend.dto.VaultCreateDTO;
import com.vaultogether.vaultogetherbackend.dto.VaultResponseDTO;
import com.vaultogether.vaultogetherbackend.dto.VaultUpdateDTO;
import com.vaultogether.vaultogetherbackend.model.Role;
import com.vaultogether.vaultogetherbackend.model.User;
import com.vaultogether.vaultogetherbackend.model.Vault;
import com.vaultogether.vaultogetherbackend.model.VaultKeyShare;
import com.vaultogether.vaultogetherbackend.model.VaultKeyShareId;
import com.vaultogether.vaultogetherbackend.model.VaultMember;
import com.vaultogether.vaultogetherbackend.model.VaultMemberId;
import com.vaultogether.vaultogetherbackend.repository.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VaultService {

  // Dependency injection with RequiredArgsConstructor
  private final VaultItemRepository vaultItemRepository;
  private final UserRepository userRepository;
  private final VaultRepository vaultRepository;
  private final VaultMemberRepository vaultMemberRepository;
  private final VaultKeyShareRepository vaultKeyShareRepository;
  private final AuditLogService auditLogService;

  // Method to create a vault
  public VaultResponseDTO createVault(Long userId, VaultCreateDTO vaultCreateDTO) {
    // Check if the User exists (else throw exception)
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    // Convert VaultCreateDTO to Vault object
    Vault vault = new Vault();
    vault.setUser(user.get());
    vault.setName(vaultCreateDTO.getName());
    vault.setDescription(vaultCreateDTO.getDescription());
    vault.setKeyVersion(0);

    // Save new Vault
    Vault savedVault = vaultRepository.save(vault);

    // Add the creator as OWNER in VaultMember
    VaultMember vaultMember = new VaultMember();
    vaultMember.setVault(savedVault);
    vaultMember.setUser(user.get());
    vaultMember.setRole(Role.OWNER);
    VaultMember savedMember = vaultMemberRepository.save(vaultMember);

    // Create the key share for the owner
    VaultKeyShare ownerKeyShare = new VaultKeyShare();
    ownerKeyShare.setVault(savedVault);
    ownerKeyShare.setUser(user.get());
    ownerKeyShare.setEncVaultKey(vaultCreateDTO.getEncryptedStringKey());
    vaultKeyShareRepository.save(ownerKeyShare);

    // Convert savedVault object to VaultResponseDTO
    VaultResponseDTO responseVault = new VaultResponseDTO();
    responseVault.setVaultId(savedVault.getVaultId());
    responseVault.setName(savedVault.getName());
    responseVault.setDescription(savedVault.getDescription());
    responseVault.setCreatedAt(savedVault.getCreatedAt());

    // Send to audit log
    auditLogService.logAction(userId, null, "VAULT_CREATE", null, savedVault.getName());

    return responseVault;
  }

  // Method to get vaults by user
  public List<VaultResponseDTO> getVaultsByUser(Long userId) {
    // Check if the User exists (Else throw exception)
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    // Call VaultRepository to find by User's UserId
    // List<Vault> vaults = vaultRepository.findByUser_UserId(userId);
    List<VaultMember> vaultMemberships = vaultMemberRepository.findByUser(user.get());

    // Convert List of Vaults to VaultResponseDTO
    // Create a list to house
    List<VaultResponseDTO> vaultResponseDTOs = new ArrayList<>();
    // Loop through each vault
    for (VaultMember vaultMember : vaultMemberships) {
      Vault vault = vaultMember.getVault();
      VaultKeyShareId vaultKeyShareId = new VaultKeyShareId(vault.getVaultId(), user.get().getUserId());
      VaultKeyShare vaultKeyShare = vaultKeyShareRepository.findById(vaultKeyShareId)
          .orElseThrow(() -> new IllegalArgumentException("VaultKeyShare not found"));

      VaultResponseDTO newResponseDTO = new VaultResponseDTO();
      newResponseDTO.setVaultId(vault.getVaultId());
      newResponseDTO.setName(vault.getName());
      newResponseDTO.setDescription(vault.getDescription());
      newResponseDTO.setCreatedAt(vault.getCreatedAt());
      newResponseDTO.setEncVaultKey(vaultKeyShare.getEncVaultKey());
      newResponseDTO.setRole(vaultMember.getRole());
      newResponseDTO.setItemCount(vaultItemRepository.countByVault(vault));
      newResponseDTO.setMemberCount(vaultMemberRepository.countByVault(vault));
      vaultResponseDTOs.add(newResponseDTO);
    }

    // Return list of VaultResponseDTO
    return vaultResponseDTOs; // Might have empty list but ok
  }

  // Method to delete a vault
  public void deleteVault(Long requestorId, Long vaultId) {

    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Find the vault
    Vault vault = vaultRepository.findById(vaultId)
        .orElseThrow(() -> new IllegalArgumentException("Vault not found"));

    // Send to audit log
    auditLogService.logAction(requestorId, null, "VAULT_DELETE", null, vault.getName());

    // Execute delete
    vaultRepository.deleteById(vaultId);

  }

  // Method to update a vault
  public VaultResponseDTO updateVault(Long requestorId, Long vaultId, VaultUpdateDTO vaultUpdateDTO) {
    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Find the vault
    Vault vault = vaultRepository.findById(vaultId)
        .orElseThrow(() -> new IllegalArgumentException("Vault not found"));

    // Update the vault
    Vault savedVault = vault;
    savedVault.setName(vaultUpdateDTO.getName());

    // Get the vault Key
    VaultKeyShareId vaultKeyShareId = new VaultKeyShareId(savedVault.getVaultId(), requestorId);
    VaultKeyShare vaultKeyShare = vaultKeyShareRepository.findById(vaultKeyShareId)
        .orElseThrow(() -> new IllegalArgumentException("Vault Key not found"));

    // Convert the vault to Vault Response DTO
    VaultResponseDTO responseDTO = new VaultResponseDTO();
    responseDTO.setVaultId(savedVault.getVaultId());
    responseDTO.setName(savedVault.getName());
    responseDTO.setCreatedAt(savedVault.getCreatedAt());
    responseDTO.setEncVaultKey(vaultKeyShare.getEncVaultKey());

    // Send to audit log
    auditLogService.logAction(requestorId, null, "VAULT_UPDATE", null, vault.getName());

    return responseDTO;
  }

  // Method to get a single vault by Vault ID
  public VaultResponseDTO getVaultById(Long userId, Long vaultId) {
    // Check if User exist
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Check if the vault exists
    Vault vault = vaultRepository.findById(vaultId)
        .orElseThrow(() -> new IllegalArgumentException("Vault not found"));

    // Check if user has access to this vault through VaultMember
    VaultMemberId memberId = new VaultMemberId(vaultId, userId);
    VaultMember vaultMember = vaultMemberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("User has no access to this vault"));

    // Get the vault key share
    VaultKeyShareId vaultKeyShareId = new VaultKeyShareId(vaultId, userId);
    VaultKeyShare vaultKeyShare = vaultKeyShareRepository.findById(vaultKeyShareId)
        .orElseThrow(() -> new IllegalArgumentException("VaultKeyShare not found"));

    // Build the response TO
    VaultResponseDTO responseDTO = new VaultResponseDTO();
    responseDTO.setVaultId(vault.getVaultId());
    responseDTO.setName(vault.getName());
    responseDTO.setDescription(vault.getDescription());
    responseDTO.setCreatedAt(vault.getCreatedAt());
    responseDTO.setEncVaultKey(vaultKeyShare.getEncVaultKey());
    responseDTO.setRole(vaultMember.getRole());
    responseDTO.setItemCount(vaultItemRepository.countByVault(vault));
    responseDTO.setMemberCount(vaultMemberRepository.countByVault(vault));
    return responseDTO;
  }

  // Helper method to validate user access to an Item
  private void validateAccess(Long vaultId, Long userId, List<Role> requiredRole) {
    // Find the VaultMember record
    VaultMemberId memberId = new VaultMemberId(vaultId, userId);
    Optional<VaultMember> vaultMember = vaultMemberRepository.findById(memberId);
    if (vaultMember.isEmpty()) {
      throw new IllegalArgumentException("User has no access");
    }

    // Get the Member's access
    Role memberRole = vaultMember.get().getRole();
    if (!requiredRole.contains(memberRole)) {
      throw new IllegalArgumentException("No Access");
    }
  }
}
