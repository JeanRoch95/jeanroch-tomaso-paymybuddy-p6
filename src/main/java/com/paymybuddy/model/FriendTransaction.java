package com.paymybuddy.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class FriendTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_transaction_id")
    private Long friendTransactionId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "fees")
    private double fees;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id")
    private User receiver;

    public FriendTransaction() {
    }

    public FriendTransaction(Long friendTransactionId, double amount, double fees, String description, Instant createdAt, User sender, User receiver) {
        this.friendTransactionId = friendTransactionId;
        this.amount = amount;
        this.fees = fees;
        this.description = description;
        this.createdAt = createdAt;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Long getFriendTransactionId() {
        return friendTransactionId;
    }

    public void setFriendTransactionId(Long friendTransactionId) {
        this.friendTransactionId = friendTransactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
