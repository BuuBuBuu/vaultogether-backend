package com.vaultogether.vaultogetherbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultogether.vaultogetherbackend.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
