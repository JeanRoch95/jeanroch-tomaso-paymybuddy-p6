package com.paymybuddy.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class FriendTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_transaction_id")
    private Long friendTransactionId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "fees")
    private BigDecimal fees;

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

    public FriendTransaction(Long friendTransactionId, BigDecimal amount, BigDecimal fees, String description, Instant createdAt, User sender, User receiver) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
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
