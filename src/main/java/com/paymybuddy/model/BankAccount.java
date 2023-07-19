package com.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BANK_ACCOUNT")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_account_id")
    private Long id;

    @NotBlank(message = "Le nom ne peux pas être vide")
    @Size(min = 2, message = "Le nom dois dépasser 2 caractères")
    @Column(name = "name")
    private String name;

    @Column(name = "iban")
    @NotBlank(message = "L'IBAN ne peux pas être vide")
    @Size(min = 12, max = 34, message = "L'IBAN doit contenir entre 12 et 34 caractères")
    private String iban;

    @Column(name = "swift")
    @NotBlank(message = "Le code SWIFT ne peux pas être vide")
    @Size(min = 8, max = 12, message = "Le code SWIFT doit contenir entre 8 et 12 caractères")
    private String swift;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "bankAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<BankTransfer> bankTransferList = new ArrayList<>();

    public BankAccount(String iban, String swift, String name, User user) {
        this.iban = iban;
        this.swift = swift;
        this.name = name;
        this.user = user;
    }

    public BankAccount() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BankTransfer> getBankTransferList() {
        return bankTransferList;
    }

    public void setBankTransferList(List<BankTransfer> bankTransferList) {
        this.bankTransferList = bankTransferList;
    }
}
