package com.vaultogether.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// RestControllerAdvice annotation says:
// This class listens for exceptions thrown by ANY controller and returned REST responds
@RestControllerAdvice
public class GlobalExceptionHandler {

  /*
    So within MethodArgumentNotValidException is a BindingResult
    (Like a report of everything that went wrong during validation)
    Usually contain things likes Field errors (single field) and
    Object errors (cross field rules)
  */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  // Spring passes in the exception into this method and we can choose what we want to do with it
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

    /*
      Try to get the infos out
      Use hashmap key value pair
      key is like "email"
      value is like "must not be blank"
      Just create this hashmap first in future then we can pass into another
      hashmap as a consolidated error json
      Try to create a frontend friendly structure
    */
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
      .getFieldErrors()
      .forEach(error -> {
        // getdefaultmessage used to get the resolved validation message defined in the constraint annotations.
        // if use getmessage it returns the technical description intended for debugging than client responses.
        errors.put(error.getField(), error.getDefaultMessage());
      });

    // Build the full response object
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value()); // get the value of the Enum 400
    response.put("error", "Validation Failed");
    response.put("validationErrors", errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.NOT_FOUND.value()); // 404
    response.put("error", "Resource Not Found");
    response.put("message", ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.FORBIDDEN.value()); // 403
    response.put("error", "Forbidden");
    response.put("message", ex.getMessage());

    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

}
