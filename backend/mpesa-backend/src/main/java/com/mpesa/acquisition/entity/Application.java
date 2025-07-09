package com.mpesa.acquisition.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bank name is required")
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @NotBlank(message = "Branch name is required")
    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @NotBlank(message = "Account name is required")
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "proof_of_bank_account")
    private String proofOfBankAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Application() {}

    public Application(String bankName, String branchName, String accountName,
                      String accountNumber, String proofOfBankAccount) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.proofOfBankAccount = proofOfBankAccount;
        this.status = ApplicationStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getProofOfBankAccount() {
        return proofOfBankAccount;
    }

    public void setProofOfBankAccount(String proofOfBankAccount) {
        this.proofOfBankAccount = proofOfBankAccount;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ApplicationStatus {
        DRAFT("Draft"),
        SUBMITTED("Submitted");

        private final String displayName;

        ApplicationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
