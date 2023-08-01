package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "BANK_TRANSFER")
public class BankTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tranfer_id")
    private Long id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "type")
    private String type;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    public BankTransfer(Long id, double amount, String description, Instant createdAt, String type, BankAccount bankAccount) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.type = type;
        this.bankAccount = bankAccount;
    }

    public BankTransfer() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}
