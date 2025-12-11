package com.vaultogether.vaultogetherbackend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "vault_items")
public class VaultItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "item_id")
  private Long itemId;

  @ManyToOne
  @JoinColumn(name = "vault_id")
  private Vault vault;

  @Column(name = "title")
  private String title;

  @Column(name = "type")
  private String type;

  @Column(name = "enc_payload")
  private String encPayload;

  @Column(name = "enc_iv")
  private String encIv;

  @Column(name = "enc_tag")
  private String encTag;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;


}
