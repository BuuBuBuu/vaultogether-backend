package com.vaultogether.vaultogetherbackend.dto;


import com.vaultogether.vaultogetherbackend.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaultMemberAddDTO {

  private String email;
  private Role role;
  private String encryptedVaultKey;

}
