package com.paymybuddy.model;

import jakarta.persistence.*;

import java.time.Instant;
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

    @Column(name = "createdAt")
    private Instant createdAt;

    @ManyToOne
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


    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
