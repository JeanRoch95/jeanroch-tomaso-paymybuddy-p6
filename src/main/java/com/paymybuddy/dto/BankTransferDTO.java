package com.paymybuddy.dto;

import com.paymybuddy.constant.TransactionTypeEnum;

import java.time.Instant;

public class BankTransferDTO {

    private Long id;
    private double amount;
    private String description;
    private Instant createdAt;
    private TransactionTypeEnum.TransactionType type;

    // Si vous voulez aussi transférer l'ID du compte bancaire lié ou d'autres informations pertinentes, ajoutez-le ici. Par exemple :
    private Long bankAccountId;

    public BankTransferDTO() {
    }

    public BankTransferDTO(Long id, double amount, String description, Instant createdAt, TransactionTypeEnum.TransactionType type, Long bankAccountId) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.type = type;
        this.bankAccountId = bankAccountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public TransactionTypeEnum.TransactionType getType() {
        return type;
    }

    public void setType(TransactionTypeEnum.TransactionType type) {
        this.type = type;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
