package com.paymybuddy.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class FriendTransactionDisplayDTO {

    private String name;

    private String description;

    private BigDecimal amount;

    private Instant createdAt;

    public FriendTransactionDisplayDTO(String name, String description, BigDecimal amount, Instant createdAt) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public FriendTransactionDisplayDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
