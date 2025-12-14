package com.vaultogether.vaultogetherbackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vaultogether.vaultogetherbackend.dto.VaultMemberAddDTO;
import com.vaultogether.vaultogetherbackend.dto.VaultMemberResponseDTO;
import com.vaultogether.vaultogetherbackend.model.Role;
import com.vaultogether.vaultogetherbackend.service.VaultMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vaultmembers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VaultMemberController {

  private final VaultMemberService vaultMemberService;

  @PostMapping("/add")
  public ResponseEntity<?> add(@RequestParam Long requestorId, @RequestParam Long vaultId,
      @Valid @RequestBody VaultMemberAddDTO vaultMemberAddDTO) {

    try {
      vaultMemberService.addMember(requestorId, vaultId, vaultMemberAddDTO);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @DeleteMapping("/{email}")
  public ResponseEntity<?> delete(@RequestParam Long requestorId, @RequestParam Long vaultId,
      @PathVariable String email) {
    try {
      vaultMemberService.removeMember(requestorId, vaultId, email);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping()
  public ResponseEntity<?> getMembers(@RequestParam Long requestorId, @RequestParam Long vaultId) {
    try {
      List<VaultMemberResponseDTO> members = vaultMemberService.getMembersByVault(requestorId, vaultId);
      return ResponseEntity.status(HttpStatus.OK).body(members);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PutMapping("/{email}/role")
  public ResponseEntity<?> updateRole(
      @RequestParam Long requestorId,
      @RequestParam Long vaultId,
      @PathVariable String email,
      @RequestParam Role newRole) {
    try {
      vaultMemberService.updateMemberRole(requestorId, vaultId, email, newRole);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
