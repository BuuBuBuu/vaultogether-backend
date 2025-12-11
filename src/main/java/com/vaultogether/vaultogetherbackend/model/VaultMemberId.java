package com.vaultogether.vaultogetherbackend.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VaultMemberId implements Serializable {

  Long vault;
  Long user;

}
