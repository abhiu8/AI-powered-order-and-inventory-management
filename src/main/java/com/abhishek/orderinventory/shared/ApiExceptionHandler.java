package com.abhishek.orderinventory.shared;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) { return response(HttpStatus.NOT_FOUND, ex.getMessage()); }
    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, Object>> badRequest(Exception ex) { return response(HttpStatus.BAD_REQUEST, ex instanceof BadRequestException ? ex.getMessage() : "Request body is invalid"); }
    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    ResponseEntity<Map<String, Object>> conflict(Exception ex) { return response(HttpStatus.CONFLICT, ex instanceof ConflictException ? ex.getMessage() : "The request conflicts with existing data"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>(); body.put("timestamp", LocalDateTime.now()); body.put("status", 400); body.put("error", "Validation failed"); body.put("details", errors);
        return ResponseEntity.badRequest().body(body);
    }
    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("timestamp", LocalDateTime.now()); body.put("status", status.value()); body.put("error", status.getReasonPhrase()); body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
