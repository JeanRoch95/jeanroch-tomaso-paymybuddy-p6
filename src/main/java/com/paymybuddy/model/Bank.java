package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

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
}
