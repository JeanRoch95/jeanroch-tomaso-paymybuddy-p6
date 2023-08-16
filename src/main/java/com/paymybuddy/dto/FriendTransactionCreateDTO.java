package com.paymybuddy.dto;

import java.math.BigDecimal;

public class FriendTransactionCreateDTO {

    private Long receiverUserId;

    private String description;

    private BigDecimal amount;

    public FriendTransactionCreateDTO() {
    }

    public FriendTransactionCreateDTO(Long receiverUserId, String description, BigDecimal amount) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
