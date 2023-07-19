package com.paymybuddy.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BANK_ACCOUNT")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_account_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "iban")
    private String iban;

    @Column(name = "swift")
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

    public BankAccount(String name, String iban, String swift, User user) {
        this.name = name;
        this.iban = iban;
        this.swift = swift;
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
