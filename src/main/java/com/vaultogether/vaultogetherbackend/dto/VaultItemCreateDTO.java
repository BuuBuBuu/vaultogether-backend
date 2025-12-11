package com.vaultogether.vaultogetherbackend.dto;

import lombok.Data;

@Data
public class VaultItemCreateDTO {

  private String title;
  private String type;
  private String username;
  private String password;
  // In case want to store other things, have a notes here keeps db schema
  // simple and encryption boundary clear
  private String notes;

}
