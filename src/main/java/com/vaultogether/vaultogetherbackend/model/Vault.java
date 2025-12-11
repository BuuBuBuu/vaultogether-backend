package com.vaultogether.vaultogetherbackend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vaults")
public class Vault {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vault_id")
  private Long vaultId;

  // User ---(One to Many)--- Vault
  @ManyToOne
  @JoinColumn(name = "user_id") // Use JoinColumn for foreign keys
  private User user;

  @Column(name = "name")
  private String name;

  @Column(name = "key_version")
  private int keyVersion;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "vault", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VaultItem> vaultItems;

  @OneToMany(mappedBy = "vault", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VaultMember> vaultMembers;

  @OneToMany(mappedBy = "vault", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VaultKeyShare> vaultKeyShares;
}
