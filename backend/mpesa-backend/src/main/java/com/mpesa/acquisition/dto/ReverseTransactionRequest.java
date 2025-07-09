package com.mpesa.acquisition.dto;

import jakarta.validation.constraints.NotBlank;

public class ReverseTransactionRequest {

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Reason is required")
    private String reason;

    // Constructors
    public ReverseTransactionRequest() {}

    public ReverseTransactionRequest(String transactionId, String reason) {
        this.transactionId = transactionId;
        this.reason = reason;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
