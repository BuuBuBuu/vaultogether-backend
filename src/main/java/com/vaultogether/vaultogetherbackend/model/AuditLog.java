package com.vaultogether.vaultogetherbackend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "audit_logs")
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id")
  private Long logId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "item_id")
  private Long vaultItemId;

  @Column(name = "item_name")
  private String vaultItemName;

  @Column(name = "action")
  private String action;

  @CreationTimestamp
  @Column(name = "at")
  private LocalDateTime at;

  @Column(name = "ip")
  private String ip;

  @Column(name = "meta")
  private String meta;

}
