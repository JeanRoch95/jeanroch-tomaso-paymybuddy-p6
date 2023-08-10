package com.paymybuddy.dto;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

public class BankAccountCreateDTO {

    @NotBlank(message = "Le nom ne peux pas être vide")
    @Size(min = 2, max = 10, message = "L'IBAN doit contenir entre 2 et 10 caractères")
    private String name;

    @NotBlank(message = "L'IBAN ne peux pas être vide")
    @Size(min = 12, max = 34, message = "Le nom doit contenir entre 12 et 34 caractères")
    private String iban;

    @NotBlank(message = "Le code SWIFT ne peux pas être vide")
    @Size(min = 8, max = 12, message = "Le code SWIFT doit contenir entre 8 et 12 caractères")
    private String swift;

    private Long userId;

    public BankAccountCreateDTO() {
    }

    public BankAccountCreateDTO(String name, String iban, String swift) {
        this.name = name;
        this.iban = iban;
        this.swift = swift;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
