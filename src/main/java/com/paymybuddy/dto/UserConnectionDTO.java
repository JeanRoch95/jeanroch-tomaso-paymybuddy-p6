package com.paymybuddy.dto;

import com.paymybuddy.model.UserConnection;

import java.time.Instant;

public class UserConnectionDTO {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private Instant createdAt;

    public UserConnectionDTO(Long id, Long senderId, Long receiverId, Instant createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
    }

    public UserConnectionDTO(UserConnection userConnection) {
    }

    public UserConnectionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
