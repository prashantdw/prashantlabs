package com.prashantlabs.documents.web;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> ex(Exception e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("BAD_REQUEST", e.getMessage()));
    }
}
