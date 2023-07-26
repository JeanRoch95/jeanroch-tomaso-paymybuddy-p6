package com.paymybuddy.dto;

import com.paymybuddy.model.User;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

public class BankAccountDTO {

    private Long id;

    @NotBlank(message = "Le nom ne peux pas être vide")
    @Size(min = 2, max = 10, message = "L'IBAN doit contenir entre 2 et 10 caractères")
    private String name;

    @NotBlank(message = "L'IBAN ne peux pas être vide")
    @Size(min = 12, max = 34, message = "L'IBAN doit contenir entre 12 et 34 caractères")
    private String iban;

    @NotBlank(message = "Le code SWIFT ne peux pas être vide")
    @Size(min = 8, max = 12, message = "Le code SWIFT doit contenir entre 8 et 12 caractères")
    private String swift;

    private Date createdAt;

    private Long userId;

    public BankAccountDTO(String iban, String swift, String name) {
        this.iban = iban;
        this.swift = swift;
        this.name = name;
    };

    public BankAccountDTO() {};

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
