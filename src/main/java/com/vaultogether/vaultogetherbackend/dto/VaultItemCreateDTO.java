package com.vaultogether.vaultogetherbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VaultItemCreateDTO {

  @NotBlank(message = "Title is required")
  @Size(max = 100, message = "Title must not exceed 100 characters")
  private String title;

  @Size(max = 16, message = "Type must not exceed 16 characters")
  private String type;

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  private String notes;

}
