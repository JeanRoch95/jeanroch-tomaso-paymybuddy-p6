package com.paymybuddy.dto;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

import java.time.Instant;
import java.util.Date;

public class BankAccountDTO {

    private Long id;

    private String name;

    private String iban;

    private String swift;

    private Instant createdAt;

    private Long userId;

    public BankAccountDTO(String iban, String swift, String name) {
        this.iban = iban;
        this.swift = swift;
        this.name = name;
    };

    public BankAccountDTO(BankAccount bankAccount) {
        this.iban = bankAccount.getIban();
        this.swift = bankAccount.getSwift();
        this.name = bankAccount.getName();
    }

    public BankAccountDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
