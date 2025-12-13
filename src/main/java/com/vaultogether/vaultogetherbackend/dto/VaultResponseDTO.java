package com.vaultogether.vaultogetherbackend.dto;

import java.time.LocalDateTime;

import com.vaultogether.vaultogetherbackend.model.Role;

import lombok.Data;

@Data
public class VaultResponseDTO {

  private Long vaultId;
  private String name;
  private String description;
  private LocalDateTime createdAt;
  private String encVaultKey;
  private Role role;
  private long itemCount;
  private long memberCount;

}
