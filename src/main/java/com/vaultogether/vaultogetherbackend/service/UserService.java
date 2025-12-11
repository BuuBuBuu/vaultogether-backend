package com.vaultogether.vaultogetherbackend.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaultogether.vaultogetherbackend.dto.UserLoginDTO;
import com.vaultogether.vaultogetherbackend.dto.UserRegisterDTO;
import com.vaultogether.vaultogetherbackend.dto.UserResponseDTO;
import com.vaultogether.vaultogetherbackend.model.User;
import com.vaultogether.vaultogetherbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
    if (userRegisterDTO == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (userRegisterDTO.getEmail() == null || userRegisterDTO.getEmail().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (userRegisterDTO.getPassword() == null || userRegisterDTO.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }
    if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    // Convert UserRegisterDTO to User object
    User user = new User();
    user.setEmail(userRegisterDTO.getEmail());
    user.setPasswordHash(passwordEncoder.encode(userRegisterDTO.getPassword()));

    // Save User object
    User savedUser = userRepository.save(user);

    // Convert User object to UserResponseDTO
    UserResponseDTO responseUser = new UserResponseDTO();
    responseUser.setUserId(savedUser.getUserId());
    responseUser.setEmail(savedUser.getEmail());
    responseUser.setCreatedAt(savedUser.getCreatedAt());
    return responseUser;
  }

  // Method for user login
  public UserResponseDTO login(UserLoginDTO userLoginDTO) {
    if (userLoginDTO == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (userLoginDTO.getEmail() == null || userLoginDTO.getEmail().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (userLoginDTO.getPassword() == null || userLoginDTO.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    // Find existing user
    Optional<User> user = userRepository.findByEmail(userLoginDTO.getEmail());
    if (user.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    // Check password is same
    if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.get().getPasswordHash())) {
      throw new IllegalArgumentException("Password incorrect");
    }

    // Convert the User to UserResponseDTO
    UserResponseDTO userResponseDTO = new UserResponseDTO();
    userResponseDTO.setUserId(user.get().getUserId());
    userResponseDTO.setEmail(user.get().getEmail());
    userResponseDTO.setCreatedAt(user.get().getCreatedAt());

    return userResponseDTO;
  }

  // Helper method to get User object from email
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Find the User entity from userReposityt
    // If not found throw new UsernameNotFoundException("User not found")
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Return the Spring User object
    return new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPasswordHash(),
      Collections.emptyList()
    );

  }

}
