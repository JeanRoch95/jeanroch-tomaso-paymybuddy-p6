package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "BANK_TRANSFER")
public class BankTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tranfer_id")
    private Long id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
}
