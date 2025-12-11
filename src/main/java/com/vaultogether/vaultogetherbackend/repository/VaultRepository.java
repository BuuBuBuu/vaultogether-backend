package com.vaultogether.vaultogetherbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultogether.vaultogetherbackend.model.Vault;

public interface VaultRepository extends JpaRepository<Vault, Long> {

  List<Vault> findByUser_UserId(Long userId);

}
