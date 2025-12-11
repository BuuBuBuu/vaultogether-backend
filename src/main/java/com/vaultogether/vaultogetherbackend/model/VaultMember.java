package com.vaultogether.vaultogetherbackend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "vault_members")
@IdClass(VaultMemberId.class)
public class VaultMember {

  @Id
  @ManyToOne
  @JoinColumn(name = "vault_id")
  private Vault vault;

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @CreationTimestamp
  @Column(name = "added_at")
  private LocalDateTime addedAt;
}
