package com.mpesa.acquisition.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Backend is running successfully");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "M-PESA Acquisition Portal Backend");
        response.put("status", "Running");
        response.put("version", "1.0.0");
        response.put("endpoints", new String[]{
            "/api/health",
            "/api/banks",
            "/api/branches?bank_id={id}",
            "/api/applications/submit",
            "/api/transaction"
        });
        return ResponseEntity.ok(response);
    }
}
