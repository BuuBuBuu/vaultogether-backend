package com.vaultogether.vaultogetherbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaultogether.vaultogetherbackend.dto.UserLoginDTO;
import com.vaultogether.vaultogetherbackend.dto.UserRegisterDTO;
import com.vaultogether.vaultogetherbackend.dto.UserResponseDTO;
import com.vaultogether.vaultogetherbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
    try {
      UserResponseDTO saved = userService.registerUser(userRegisterDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
    try {
      UserResponseDTO response = userService.login(userLoginDTO);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
