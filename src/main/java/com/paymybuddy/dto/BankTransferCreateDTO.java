package com.paymybuddy.dto;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.model.BankTransfer;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BankTransferCreateDTO {

    @NotBlank(message = "Veuillez choisir un compte")
    private String iban;

    private String description;

    private BigDecimal amount;

    @NotNull(message = "Veuillez choisir une action a effectuer sur votre compte")
    private TransactionTypeEnum.TransactionType type;

    public BankTransferCreateDTO(String iban, String description, BigDecimal amount, TransactionTypeEnum.TransactionType type) {
        this.iban = iban;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public BankTransferCreateDTO(String iban, String description, BigDecimal amount) {
        this.iban = iban;
        this.description = description;
        this.amount = amount;
    }

    public BankTransferCreateDTO() {
    }

    public BankTransferCreateDTO(BankTransfer bankTransfer) {
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
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

    public TransactionTypeEnum.TransactionType getType() {
        return type;
    }

    public void setType(TransactionTypeEnum.TransactionType type) {
        this.type = type;
    }
}
