package com.vaultogether.vaultogetherbackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vaultogether.vaultogetherbackend.dto.VaultCreateDTO;
import com.vaultogether.vaultogetherbackend.dto.VaultResponseDTO;
import com.vaultogether.vaultogetherbackend.service.VaultService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/vaults")
@RequiredArgsConstructor
public class VaultController {

  private final VaultService vaultService;

  @PostMapping("/create")
  public ResponseEntity<?> create(@RequestBody VaultCreateDTO vaultCreateDTO, @RequestParam Long userId) {

    try {
      VaultResponseDTO response = vaultService.createVault(userId, vaultCreateDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping()
  public ResponseEntity<List<VaultResponseDTO>> getVaults(@RequestParam Long userId) {
    List<VaultResponseDTO> vaults = vaultService.getVaultsByUser(userId);
    return ResponseEntity.status(HttpStatus.OK).body(vaults);
  }

}
