package com.vaultogether.vaultogetherbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vault_key_shares")
@IdClass(VaultKeyShareId.class)
public class VaultKeyShare {

  @Id
  @ManyToOne
  @JoinColumn(name = "vault_id")
  private Vault vault;

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "enc_vault_key")
  private String encVaultKey;
}
