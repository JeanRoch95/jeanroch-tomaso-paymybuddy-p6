package com.paymybuddy.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "USER_CONNECTION")
public class UserConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "user_id")
    private User receiver;

    @Column(name = "created_at")
    private Instant createdAt;

    public UserConnection() {
    }

    public UserConnection(Long id, User sender, User receiver, Instant createdAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
