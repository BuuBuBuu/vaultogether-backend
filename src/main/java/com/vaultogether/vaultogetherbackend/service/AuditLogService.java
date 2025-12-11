package com.vaultogether.vaultogetherbackend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaultogether.vaultogetherbackend.model.AuditLog;
import com.vaultogether.vaultogetherbackend.model.User;
import com.vaultogether.vaultogetherbackend.model.Vault;
import com.vaultogether.vaultogetherbackend.model.VaultItem;
import com.vaultogether.vaultogetherbackend.repository.AuditLogRepository;
import com.vaultogether.vaultogetherbackend.repository.UserRepository;
import com.vaultogether.vaultogetherbackend.repository.VaultItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;
  private final UserRepository userRepository;
  private final VaultItemRepository vaultItemRepository;

  // method to log the action
  public void logAction(Long userId, Long itemId, String action, String ip, String meta) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    VaultItem vaultItem;
    if (itemId != null) {
      Optional<VaultItem> foundItem = vaultItemRepository.findById(itemId);
      if (foundItem.isEmpty()) {
        throw new IllegalArgumentException("Vault Item not found");
      }
      vaultItem = foundItem.get();
    } else {
      vaultItem = null;
    }

     // Create audit log object
    AuditLog auditLog = new AuditLog();
    auditLog.setUser(user.get());
    auditLog.setVaultItem(vaultItem);
    auditLog.setAction(action);
    auditLog.setIp(ip);
    auditLog.setMeta(meta);

    // Save the audit log object
    auditLogRepository.save(auditLog);

  }

}
