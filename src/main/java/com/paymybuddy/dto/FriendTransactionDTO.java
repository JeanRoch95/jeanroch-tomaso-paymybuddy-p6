package com.paymybuddy.dto;

import java.time.Instant;

public class FriendTransactionDTO {

    private Long friendTransactionId;

    private double amount;

    private double fees;

    private String description;

    private Instant createdAt;

    private Long senderConnectionId;

    private Long receiverConnectionId;

    public FriendTransactionDTO() {
    }

    public FriendTransactionDTO(Long friendTransactionId, double amount, double fees, String description, Instant createdAt, Long senderConnectionId, Long receiverConnectionId) {
        this.friendTransactionId = friendTransactionId;
        this.amount = amount;
        this.fees = fees;
        this.description = description;
        this.createdAt = createdAt;
        this.senderConnectionId = senderConnectionId;
        this.receiverConnectionId = receiverConnectionId;
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

    public Long getSenderConnectionId() {
        return senderConnectionId;
    }

    public void setSenderConnectionId(Long senderConnectionId) {
        this.senderConnectionId = senderConnectionId;
    }

    public Long getReceiverConnectionId() {
        return receiverConnectionId;
    }

    public void setReceiverConnectionId(Long receiverConnectionId) {
        this.receiverConnectionId = receiverConnectionId;
    }
}
