package com.vaultogether.vaultogetherbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaultogether.vaultogetherbackend.dto.VaultMemberAddDTO;
import com.vaultogether.vaultogetherbackend.model.Role;
import com.vaultogether.vaultogetherbackend.model.User;
import com.vaultogether.vaultogetherbackend.model.Vault;
import com.vaultogether.vaultogetherbackend.model.VaultKeyShare;
import com.vaultogether.vaultogetherbackend.model.VaultKeyShareId;
import com.vaultogether.vaultogetherbackend.model.VaultMember;
import com.vaultogether.vaultogetherbackend.model.VaultMemberId;
import com.vaultogether.vaultogetherbackend.repository.UserRepository;
import com.vaultogether.vaultogetherbackend.repository.VaultKeyShareRepository;
import com.vaultogether.vaultogetherbackend.repository.VaultMemberRepository;
import com.vaultogether.vaultogetherbackend.repository.VaultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VaultMemberService {

  // Autowire dependencies
  private final VaultRepository vaultRepository;
  private final UserRepository userRepository;
  private final VaultMemberRepository vaultMemberRepository;
  private final VaultKeyShareRepository vaultKeyShareRepository;
  private final AuditLogService auditLogService;


  // Method to add member to vault
  public void addMember(Long requestorId, Long vaultId, VaultMemberAddDTO vaultMemberAddDTO) {
    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Check whether there is vault with the provided vaultId
    Optional<Vault> vault = vaultRepository.findById(vaultId);
    if (vault.isEmpty()) {
      throw new IllegalArgumentException("Vault not found");
    }

    // Find the user using the provided email
    Optional<User> user = userRepository.findByEmail(vaultMemberAddDTO.getEmail());
    if (user.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    // Check whether the user already exists as a member of the vault
    boolean exists = vaultMemberRepository.existsByVaultAndUser(vault.get(), user.get());
    if (exists) {
      throw new IllegalArgumentException("User already a member of vault");
    }

    // Save new record to the vault member table
    VaultMember vaultMember = new VaultMember();
    vaultMember.setVault(vault.get());
    vaultMember.setUser(user.get());
    vaultMember.setRole(vaultMemberAddDTO.getRole());

    vaultMemberRepository.save(vaultMember);

    // Save a record to the Vault Key Share table too
    VaultKeyShare memberKeyShare = new VaultKeyShare();
    memberKeyShare.setVault(vault.get());
    memberKeyShare.setUser(user.get());
    memberKeyShare.setEncVaultKey(vaultMemberAddDTO.getEncryptedVaultKey());

    vaultKeyShareRepository.save(memberKeyShare);

    // Send to audit log
    auditLogService.logAction(requestorId, null, "ADD_MEMBER", null, vault.get().getName() + " " + user.get().getEmail());

  }

  // Method to remove member
  public void removeMember(Long requestorId, Long vaultId, String email) {
    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Check if the vault exists
    Vault vault = vaultRepository.findById(vaultId)
      .orElseThrow(() -> new IllegalArgumentException("Vault not found"));

    // Check that the user exists through email
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Check if the member exists
    VaultMemberId vaultMemberId = new VaultMemberId(vaultId, user.getUserId());
    VaultMember vaultMember = vaultMemberRepository.findById(vaultMemberId)
      .orElseThrow(() -> new IllegalArgumentException("Vault member not found"));

    // Find vault key of the vault member
    VaultKeyShareId vaultKeyShareId = new VaultKeyShareId(vaultId, user.getUserId());
    VaultKeyShare vaultKeyShare = vaultKeyShareRepository.findById(vaultKeyShareId)
      .orElseThrow(() -> new IllegalArgumentException("Vault key not found"));

    // Send to audit log
    auditLogService.logAction(requestorId, null, "REMOVE_MEMBER", null, vault.getName() + " " + user.getEmail());

    // Now that we found everything we have to delete everything
    vaultMemberRepository.deleteById(vaultMemberId);
    vaultKeyShareRepository.deleteById(vaultKeyShareId);



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
