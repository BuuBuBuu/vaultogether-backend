package com.vaultogether.vaultogetherbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultogether.vaultogetherbackend.model.VaultKeyShare;
import com.vaultogether.vaultogetherbackend.model.VaultKeyShareId;

public interface VaultKeyShareRepository extends JpaRepository<VaultKeyShare, VaultKeyShareId> {

}
