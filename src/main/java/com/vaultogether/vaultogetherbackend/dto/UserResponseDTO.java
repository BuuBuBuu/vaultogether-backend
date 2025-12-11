package com.vaultogether.vaultogetherbackend.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponseDTO {

  private Long userId;
  private String email;
  private LocalDateTime createdAt;

}
