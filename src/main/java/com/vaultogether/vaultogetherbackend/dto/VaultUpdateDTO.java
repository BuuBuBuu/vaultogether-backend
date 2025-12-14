package com.vaultogether.vaultogetherbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VaultUpdateDTO {

  @NotBlank(message = "Vault name is required")
  @Size(max = 100, message = "Vault name must not exceed 100 characters")
  private String name;

}
