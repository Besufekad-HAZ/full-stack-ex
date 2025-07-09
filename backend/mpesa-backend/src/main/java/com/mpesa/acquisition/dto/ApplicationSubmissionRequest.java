package com.mpesa.acquisition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationSubmissionRequest {

    @NotNull(message = "Bank ID is required")
    private Long bankId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private String proofOfBankAccount;

    @NotBlank(message = "Status is required")
    private String status;

    // Constructors
    public ApplicationSubmissionRequest() {}

    public ApplicationSubmissionRequest(Long bankId, Long branchId, String bankName,
                                      String branchName, String accountName,
                                      String accountNumber, String proofOfBankAccount,
                                      String status) {
        this.bankId = bankId;
        this.branchId = branchId;
        this.bankName = bankName;
        this.branchName = branchName;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.proofOfBankAccount = proofOfBankAccount;
        this.status = status;
    }

    // Getters and Setters
    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
