package com.paymybuddy.dto;

import com.paymybuddy.model.BankTransfer;

public class BankTransferDTO {

    private String iban;

    private String description;

    private double amount;

    public BankTransferDTO(String iban, String description, double amount) {
        this.iban = iban;
        this.description = description;
        this.amount = amount;
    }

    public BankTransferDTO() {
    }

    public BankTransferDTO(BankTransfer bankTransfer) {
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
}
