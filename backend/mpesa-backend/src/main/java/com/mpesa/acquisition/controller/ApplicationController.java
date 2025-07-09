package com.mpesa.acquisition.controller;

import com.mpesa.acquisition.dto.ApplicationSubmissionRequest;
import com.mpesa.acquisition.entity.Application;
import com.mpesa.acquisition.entity.Bank;
import com.mpesa.acquisition.entity.Branch;
import com.mpesa.acquisition.repository.ApplicationRepository;
import com.mpesa.acquisition.repository.BankRepository;
import com.mpesa.acquisition.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BranchRepository branchRepository;

    /**
     * POST /api/applications/submit - Submit the application
     */
    @PostMapping("/applications/submit")
    public ResponseEntity<Map<String, Object>> submitApplication(@Valid @RequestBody ApplicationSubmissionRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate bank exists
            Optional<Bank> bank = bankRepository.findById(request.getBankId());
            if (bank.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Invalid bank selected");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate branch exists and belongs to the bank
            Optional<Branch> branch = branchRepository.findById(request.getBranchId());
            if (branch.isEmpty() || !branch.get().getBankId().equals(request.getBankId())) {
                response.put("status", "FAILED");
                response.put("message", "Invalid branch selected for the specified bank");
                return ResponseEntity.badRequest().body(response);
            }

            // Prevent duplicating account numbers
            if (applicationRepository.existsByAccountNumber(request.getAccountNumber())) {
                response.put("status", "FAILED");
                response.put("message", "Account number already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new application
            Application application = new Application();
            application.setBankName(request.getBankName());
            application.setBranchName(request.getBranchName());
            application.setAccountName(request.getAccountName());
            application.setAccountNumber(request.getAccountNumber());
            application.setProofOfBankAccount(request.getProofOfBankAccount());

            // Set status based on request
            if ("SUBMITTED".equalsIgnoreCase(request.getStatus())) {
                application.setStatus(Application.ApplicationStatus.SUBMITTED);
            } else {
                application.setStatus(Application.ApplicationStatus.DRAFT);
            }

            // Save application
            Application savedApplication = applicationRepository.save(application);

            // Prepare response
            response.put("status", "SUCCESS");
            response.put("message", "Application " + savedApplication.getStatus().getDisplayName().toLowerCase() + " successfully");
            response.put("applicationId", savedApplication.getId());
            response.put("accountNumber", savedApplication.getAccountNumber());
            response.put("applicationStatus", savedApplication.getStatus().getDisplayName());
            response.put("submissionDate", savedApplication.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Application submission failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/applications/{id} - Get application by ID
     */
    @GetMapping("/applications/{id}")
    public ResponseEntity<Map<String, Object>> getApplicationById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Application> application = applicationRepository.findById(id);
            if (application.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Application not found");
                return ResponseEntity.notFound().build();
            }

            response.put("status", "SUCCESS");
            response.put("application", application.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Failed to retrieve application");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/applications - Get all applications
     */
    @GetMapping("/applications")
    public ResponseEntity<Map<String, Object>> getAllApplications() {
        Map<String, Object> response = new HashMap<>();

        try {
            var applications = applicationRepository.findAll();
            response.put("status", "SUCCESS");
            response.put("applications", applications);
            response.put("count", applications.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Failed to retrieve applications");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/applications/account/{accountNumber} - Get application by account number
     */
    @GetMapping("/applications/account/{accountNumber}")
    public ResponseEntity<Map<String, Object>> getApplicationByAccountNumber(@PathVariable String accountNumber) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Application> application = applicationRepository.findByAccountNumber(accountNumber);
            if (application.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Application not found for account number: " + accountNumber);
                return ResponseEntity.notFound().build();
            }

            response.put("status", "SUCCESS");
            response.put("application", application.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Failed to retrieve application");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
