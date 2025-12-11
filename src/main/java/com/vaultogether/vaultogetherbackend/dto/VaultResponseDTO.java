package com.vaultogether.vaultogetherbackend.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VaultResponseDTO {

  private Long vaultId;
  private String name;
  private LocalDateTime createdAt;
  private String encVaultKey;

}
