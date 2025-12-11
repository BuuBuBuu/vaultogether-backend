package com.vaultogether.vaultogetherbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultogether.vaultogetherbackend.model.VaultItem;

public interface VaultItemRepository extends JpaRepository<VaultItem, Long> {

  List<VaultItem> findByVault_VaultId(Long vaultId);

}
