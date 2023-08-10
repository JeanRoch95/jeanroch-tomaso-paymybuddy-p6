package com.paymybuddy.dto;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.model.BankTransfer;

public class BankTransferCreateDTO {

    private String iban;

    private String description;

    private double amount;

    private TransactionTypeEnum.TransactionType type = TransactionTypeEnum.TransactionType.DEBIT;

    public BankTransferCreateDTO(String iban, String description, double amount, TransactionTypeEnum.TransactionType type) {
        this.iban = iban;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public BankTransferCreateDTO(String iban, String description, double amount) {
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionTypeEnum.TransactionType getType() {
        return type;
    }

    public void setType(TransactionTypeEnum.TransactionType type) {
        this.type = type;
    }
}
