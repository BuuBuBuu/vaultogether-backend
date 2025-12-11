package com.vaultogether.vaultogetherbackend.dto;

import lombok.Data;

@Data
public class VaultCreateDTO {

  private String name;
  private String encryptedStringKey;

}
