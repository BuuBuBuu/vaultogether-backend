package com.vaultogether.vaultogetherbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VaultItemResponseDTO {

  private Long itemId;
  private String title;
  private String type;
  private String username;
  private String password;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
