package com.vaultogether.vaultogetherbackend.dto;

import java.time.LocalDateTime;

import com.vaultogether.vaultogetherbackend.model.Role;

import lombok.Data;

@Data
public class VaultMemberResponseDTO {
  private Long userId;
  private String email;
  private Role role;
  private LocalDateTime addedAt;

}
