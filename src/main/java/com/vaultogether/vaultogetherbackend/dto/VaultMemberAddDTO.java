package com.vaultogether.vaultogetherbackend.dto;


import com.vaultogether.vaultogetherbackend.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaultMemberAddDTO {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotNull(message = "Role is required")
  private Role role;

  @NotBlank(message = "Encrypted vault key is required")
  private String encryptedVaultKey;

}
