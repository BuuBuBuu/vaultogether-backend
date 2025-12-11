package com.vaultogether.vaultogetherbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vaultogether.vaultogetherbackend.dto.VaultMemberAddDTO;
import com.vaultogether.vaultogetherbackend.service.VaultMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vaultmembers")
@RequiredArgsConstructor
public class VaultMemberController {

  private final VaultMemberService vaultMemberService;

  @PostMapping("/add")
  public ResponseEntity<?> add(@RequestParam Long requestorId, @RequestParam Long vaultId,
    @RequestBody VaultMemberAddDTO vaultMemberAddDTO) {

    try {
      vaultMemberService.addMember(requestorId, vaultId, vaultMemberAddDTO);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }


}
