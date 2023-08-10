package com.paymybuddy.dto;

public class FriendTransactionCreateDTO {

    private Long receiverUserId;

    private String description;

    private double amount; // TODO Vérifier coté serveur que l'utilisateur entre bien les données souhaitées

    public FriendTransactionCreateDTO() {
    }

    public FriendTransactionCreateDTO(Long receiverUserId, String description, double amount) {
        this.receiverUserId = receiverUserId;
        this.description = description;
        this.amount = amount;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
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