package com.vaultogether.exception;

/**
 * ResourceNotFoundException custom exception that extends RuntimeException
 * This custom exception extends RuntimeException makes it an unchecked
 * So that no need throws declarations everywhere
 * Spring will automatically handle it resulting in cleaner codes
 */
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
