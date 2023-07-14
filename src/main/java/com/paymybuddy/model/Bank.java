package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "BANK")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Long id;

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
            mappedBy = "bank",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<BankTransfer> bankTransferList = new ArrayList<>();
}
