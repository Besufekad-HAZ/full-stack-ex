package com.mpesa.acquisition.controller;

import com.mpesa.acquisition.dto.TransactionRequest;
import com.mpesa.acquisition.dto.ReverseTransactionRequest;
import com.mpesa.acquisition.entity.Application;
import com.mpesa.acquisition.entity.TransactionHistory;
import com.mpesa.acquisition.repository.ApplicationRepository;
import com.mpesa.acquisition.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * POST /api/transaction - Accept a transaction payload
     */
    @PostMapping("/transaction")
    public ResponseEntity<Map<String, Object>> processTransaction(@Valid @RequestBody TransactionRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate account number exists in tbl_application with status SUBMITTED
            Optional<Application> application = applicationRepository.findByAccountNumber(request.getAccountNumber());
            if (application.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Account number not found");
                return ResponseEntity.badRequest().body(response);
            }

            if (application.get().getStatus() != Application.ApplicationStatus.SUBMITTED) {
                response.put("status", "FAILED");
                response.put("message", "Account not approved for transactions");
                return ResponseEntity.badRequest().body(response);
            }

            // Generate unique transaction ID
            String transactionId = "TX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

            // Create transaction history entry
            TransactionHistory transaction = new TransactionHistory();
            transaction.setTransactionId(transactionId);
            transaction.setAccountNumber(request.getAccountNumber());
            transaction.setAmount(request.getAmount());
            transaction.setNarration(request.getNarration());
            transaction.setStatus(TransactionHistory.TransactionStatus.SUCCESS);

            // Save transaction
            TransactionHistory savedTransaction = transactionHistoryRepository.save(transaction);

            // Prepare response
            response.put("status", "SUCCESS");
            response.put("transactionId", savedTransaction.getTransactionId());
            response.put("accountNumber", savedTransaction.getAccountNumber());
            response.put("amount", savedTransaction.getAmount());
            response.put("narration", savedTransaction.getNarration());
            response.put("timestamp", savedTransaction.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Create failed transaction entry
            try {
                String transactionId = "TX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
                TransactionHistory failedTransaction = new TransactionHistory();
                failedTransaction.setTransactionId(transactionId);
                failedTransaction.setAccountNumber(request.getAccountNumber());
                failedTransaction.setAmount(request.getAmount());
                failedTransaction.setNarration(request.getNarration());
                failedTransaction.setStatus(TransactionHistory.TransactionStatus.FAILED);
                transactionHistoryRepository.save(failedTransaction);
            } catch (Exception ex) {
                // Log exception
            }

            response.put("status", "FAILED");
            response.put("message", "Transaction processing failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/reverse - Reverse a transaction
     */
    @PostMapping("/reverse")
    public ResponseEntity<Map<String, Object>> reverseTransaction(@Valid @RequestBody ReverseTransactionRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate if transaction exists and is reversible
            Optional<TransactionHistory> transaction = transactionHistoryRepository.findByTransactionId(request.getTransactionId());
            if (transaction.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Transaction not found");
                return ResponseEntity.badRequest().body(response);
            }

            TransactionHistory existingTransaction = transaction.get();

            // Check if transaction is already reversed
            if (existingTransaction.getStatus() == TransactionHistory.TransactionStatus.REVERSED) {
                response.put("status", "FAILED");
                response.put("message", "Transaction already reversed");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if transaction can be reversed (only SUCCESS transactions can be reversed)
            if (existingTransaction.getStatus() != TransactionHistory.TransactionStatus.SUCCESS) {
                response.put("status", "FAILED");
                response.put("message", "Only successful transactions can be reversed");
                return ResponseEntity.badRequest().body(response);
            }

            // Update transaction status to REVERSED
            existingTransaction.setStatus(TransactionHistory.TransactionStatus.REVERSED);
            existingTransaction.setNarration(existingTransaction.getNarration() + " - REVERSED: " + request.getReason());

            TransactionHistory updatedTransaction = transactionHistoryRepository.save(existingTransaction);

            // Prepare response
            response.put("status", "SUCCESS");
            response.put("transactionId", updatedTransaction.getTransactionId());
            response.put("accountNumber", updatedTransaction.getAccountNumber());
            response.put("amount", updatedTransaction.getAmount());
            response.put("reversalReason", request.getReason());
            response.put("timestamp", updatedTransaction.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Transaction reversal failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/transactions/{accountNumber} - Get transaction history for an account
     */
    @GetMapping("/transactions/{accountNumber}")
    public ResponseEntity<Map<String, Object>> getTransactionHistory(@PathVariable String accountNumber) {
        Map<String, Object> response = new HashMap<>();

        try {
            var transactions = transactionHistoryRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber);
            response.put("status", "SUCCESS");
            response.put("accountNumber", accountNumber);
            response.put("transactions", transactions);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Failed to retrieve transaction history");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
