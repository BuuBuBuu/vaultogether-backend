package com.vaultogether.vaultogetherbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultogether.vaultogetherbackend.model.User;
import com.vaultogether.vaultogetherbackend.model.Vault;
import com.vaultogether.vaultogetherbackend.model.VaultMember;
import com.vaultogether.vaultogetherbackend.model.VaultMemberId;

public interface VaultMemberRepository extends JpaRepository<VaultMember, VaultMemberId> {

  boolean existsByVaultAndUser(Vault vault, User user);
  List<VaultMember> findByUser(User user);
  long countByVault(Vault vault);
  List<VaultMember> findByVault(Vault vault);

}
