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

import com.vaultogether.vaultogetherbackend.dto.VaultItemCreateDTO;
import com.vaultogether.vaultogetherbackend.dto.VaultItemResponseDTO;
import com.vaultogether.vaultogetherbackend.service.VaultItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/vaults/{vaultId}/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VaultItemController {

  private final VaultItemService vaultItemService;

  @GetMapping()
  public ResponseEntity<?> getItems(@RequestParam Long requestorId, @PathVariable Long vaultId) {

    try {
      List<VaultItemResponseDTO> response = vaultItemService.getItemsByVault(requestorId, vaultId);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping()
  public ResponseEntity<?> create(@RequestParam Long requestorId, @PathVariable Long vaultId,
    @RequestBody VaultItemCreateDTO vaultItemCreateDTO) {

    try {
      VaultItemResponseDTO response = vaultItemService.createItem(requestorId, vaultId, vaultItemCreateDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<?> delete(@RequestParam Long requestorId, @PathVariable Long vaultId,
    @PathVariable Long itemId) {

    try {
      vaultItemService.deleteItem(requestorId, vaultId, itemId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<?> update(@RequestParam Long requestorId, @PathVariable Long vaultId,
    @PathVariable Long itemId, @RequestBody VaultItemCreateDTO vaultItemCreateDTO) {

      try {
        VaultItemResponseDTO response = vaultItemService.updateItem(requestorId, vaultId, itemId, vaultItemCreateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
    }
}
