package com.vaultogether.vaultogetherbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VaultCreateDTO {

  @NotBlank(message = "Vault name is required")
  @Size(max = 100, message = "Vault name must not exceed 100 characters")
  private String name;

  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;

  @NotBlank(message = "Encrypted vault key is required")
  private String encryptedStringKey;
}
