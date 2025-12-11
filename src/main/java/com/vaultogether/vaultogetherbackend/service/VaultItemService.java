package com.vaultogether.vaultogetherbackend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaultogether.vaultogetherbackend.dto.VaultItemResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaultogether.vaultogetherbackend.dto.VaultItemCreateDTO;
import com.vaultogether.vaultogetherbackend.model.Role;
import com.vaultogether.vaultogetherbackend.model.Vault;
import com.vaultogether.vaultogetherbackend.model.VaultItem;
import com.vaultogether.vaultogetherbackend.model.VaultMember;
import com.vaultogether.vaultogetherbackend.model.VaultMemberId;
import com.vaultogether.vaultogetherbackend.repository.VaultItemRepository;
import com.vaultogether.vaultogetherbackend.repository.VaultMemberRepository;
import com.vaultogether.vaultogetherbackend.repository.VaultRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class VaultItemService {

  private final VaultRepository vaultRepository;
  private final VaultItemRepository vaultItemRepository;
  private final VaultMemberRepository vaultMemberRepository;
  private final AuditLogService auditLogService;
  private final JsonMapper jsonMapper;

  // Method to get vault item
  public List<VaultItemResponseDTO> getItemsByVault(Long requestorId, Long vaultId) {

    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    requiredRoles.add(Role.EDITOR);
    requiredRoles.add(Role.VIEWER);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Check that the vault exists
    Optional<Vault> vault = vaultRepository.findById(vaultId);
    if (vault.isEmpty()) {
      throw new IllegalArgumentException("Vault not found");
    }

    // Call method from repository to get the list of data
    List<VaultItem> vaultItems = vaultItemRepository.findByVault_VaultId(vaultId);
    List<VaultItemResponseDTO> vaultItemResponseDTOs = new ArrayList<>();
    for (VaultItem vaultItem : vaultItems) {
      // Convert the saved entity to a response DTO
      VaultItemResponseDTO responseDTO = convertToDTO(vaultItem);
      vaultItemResponseDTOs.add(responseDTO);
    }

    return vaultItemResponseDTOs;
  }

  // Method to create vault item
  public VaultItemResponseDTO createItem(Long requestorId, Long vaultId, VaultItemCreateDTO vaultItemCreateDTO) {

    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    requiredRoles.add(Role.EDITOR);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Check that the vault exists
    Optional<Vault> vault = vaultRepository.findById(vaultId);
    if (vault.isEmpty()) {
      throw new IllegalArgumentException("Vault not found");
    }

    // Use JsonMapper to turn to JSON strings to store properly
    Map<String, String> stuff = new HashMap<>();
    stuff.put("username", vaultItemCreateDTO.getUsername());
    stuff.put("password", vaultItemCreateDTO.getPassword());
    stuff.put("notes", vaultItemCreateDTO.getNotes());

    String jsonPayload = jsonMapper.writeValueAsString(stuff);

    // If vault found, translate the VaultItemCreateDTO details to VaultItem entity
    // to prepare for saving
    VaultItem vaultItem = new VaultItem();
    vaultItem.setVault(vault.get());
    vaultItem.setTitle(vaultItemCreateDTO.getTitle());
    vaultItem.setType(vaultItemCreateDTO.getType());
    vaultItem.setEncPayload(jsonPayload);

    // Save the new vault item to the database
    VaultItem savedVaultItem = vaultItemRepository.save(vaultItem);

    // Convert the saved entity to a response DTO
    VaultItemResponseDTO responseDTO = convertToDTO(savedVaultItem);

    // Send to audit log
    auditLogService.logAction(requestorId, savedVaultItem.getItemId(), "ITEM_CREATE", null, savedVaultItem.getTitle());

    return responseDTO;
  }


  // Method to delete item
  public void deleteItem(Long requestorId, Long vaultId, Long itemId) {

    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    requiredRoles.add(Role.EDITOR);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Find the item
    Optional<VaultItem> vaultItem = vaultItemRepository.findById(itemId);
    // Verify existence
    if (vaultItem.isEmpty()) {
      throw new IllegalArgumentException("Item not found");
    }

    // Verify whether the item belongs to this vault
    if (!vaultItem.get().getVault().getVaultId().equals(vaultId)) {
      throw new IllegalArgumentException("Item not found");
    }

    // Send to audit log
    auditLogService.logAction(requestorId, vaultItem.get().getItemId(), "ITEM_DELETE", null,
        vaultItem.get().getTitle());

    // Execute delete
    vaultItemRepository.deleteById(itemId);
  }

  // Method to update item
  public VaultItemResponseDTO updateItem(Long requestorId, Long vaultId, Long itemId,
      VaultItemCreateDTO vaultItemCreateDTO) {

    // Check access of requestor
    List<Role> requiredRoles = new ArrayList<>();
    requiredRoles.add(Role.OWNER);
    requiredRoles.add(Role.EDITOR);
    validateAccess(vaultId, requestorId, requiredRoles);

    // Verify vault exists
    Optional<Vault> vault = vaultRepository.findById(vaultId);
    if (vault.isEmpty()) {
      throw new IllegalArgumentException("Vault not found");
    }

    // Find the existing item
    Optional<VaultItem> existingItem = vaultItemRepository.findById(itemId);
    if (existingItem.isEmpty()) {
      throw new IllegalArgumentException("Item not found");
    }

    // Verify whether the item belongs to this vault
    if (!existingItem.get().getVault().getVaultId().equals(vaultId)) {
      throw new IllegalArgumentException("Item not found");
    }

    VaultItem vaultItem = existingItem.get();

    // Handle the Json
    Map<String, String> stuff = new HashMap<>();
    stuff.put("username", vaultItemCreateDTO.getUsername());
    stuff.put("password", vaultItemCreateDTO.getPassword());
    stuff.put("notes", vaultItemCreateDTO.getNotes());

    String jsonPayload = jsonMapper.writeValueAsString(stuff);

    // Update fields
    vaultItem.setVault(vault.get());
    vaultItem.setTitle(vaultItemCreateDTO.getTitle());
    vaultItem.setType(vaultItemCreateDTO.getType());
    vaultItem.setEncPayload(jsonPayload);

    // Save the updated vaultItem to database
    VaultItem savedVaultItem = vaultItemRepository.save(vaultItem);

    // Convert the savedVaultItem back to the ResponseDTO format
    VaultItemResponseDTO responseDTO = convertToDTO(savedVaultItem);

    // Send to audit log
    auditLogService.logAction(requestorId, savedVaultItem.getItemId(), "ITEM_UPDATE", null, savedVaultItem.getTitle());

    return responseDTO;

  }

  // Helper method to convert from VaultItem to the response dto
  private VaultItemResponseDTO convertToDTO(VaultItem vaultItem) {
    VaultItemResponseDTO responseDTO = new VaultItemResponseDTO();
    responseDTO.setItemId(vaultItem.getItemId());
    responseDTO.setTitle(vaultItem.getTitle());
    responseDTO.setType(vaultItem.getType());
    responseDTO.setCreatedAt(vaultItem.getCreatedAt());
    responseDTO.setUpdatedAt(vaultItem.getUpdatedAt());

    Map<String, String> payloadMap = jsonMapper.readValue(
        vaultItem.getEncPayload(),
        new TypeReference<Map<String, String>>() {
        });

    responseDTO.setUsername(payloadMap.get("username"));
    responseDTO.setPassword(payloadMap.get("password"));
    responseDTO.setNotes(payloadMap.get("notes"));

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
