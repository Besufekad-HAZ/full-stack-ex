package com.mpesa.acquisition.controller;

import com.mpesa.acquisition.entity.Bank;
import com.mpesa.acquisition.entity.Branch;
import com.mpesa.acquisition.repository.BankRepository;
import com.mpesa.acquisition.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class BankController {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BranchRepository branchRepository;

    /**
     * GET /api/banks - Fetch all bank names from tbl_bank
     */
    @GetMapping("/banks")
    public ResponseEntity<List<Bank>> getAllBanks() {
        try {
            List<Bank> banks = bankRepository.findAllOrderByValue();
            return ResponseEntity.ok(banks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/branches?bank_id={id} - Fetch all branches for a specific bank
     */
    @GetMapping("/branches")
    public ResponseEntity<List<Branch>> getBranchesByBankId(@RequestParam("bank_id") Long bankId) {
        try {
            // Verify bank exists
            Optional<Bank> bank = bankRepository.findById(bankId);
            if (bank.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Branch> branches = branchRepository.findByBankIdOrderByValue(bankId);
            return ResponseEntity.ok(branches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/banks/{id} - Get bank by ID
     */
    @GetMapping("/banks/{id}")
    public ResponseEntity<Bank> getBankById(@PathVariable Long id) {
        try {
            Optional<Bank> bank = bankRepository.findById(id);
            if (bank.isPresent()) {
                return ResponseEntity.ok(bank.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/branches/{id} - Get branch by ID
     */
    @GetMapping("/branches/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable Long id) {
        try {
            Optional<Branch> branch = branchRepository.findById(id);
            if (branch.isPresent()) {
                return ResponseEntity.ok(branch.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
