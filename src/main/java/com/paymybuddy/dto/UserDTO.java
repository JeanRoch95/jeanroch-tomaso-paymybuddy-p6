package com.paymybuddy.dto;

import java.time.Instant;
import java.util.List;

public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Double balance;

    private Instant createdAt;

    private Instant updatedAt;

    private List<BankAccountDTO> bankAccountList;

    private List<UserConnectionDTO> connections;

    public UserDTO() {
    }

    public UserDTO(Long id, String firstName, String lastName, String email, Double balance, Instant createdAt, Instant updatedAt, List<BankAccountDTO> bankAccountList, List<UserConnectionDTO> connections) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bankAccountList = bankAccountList;
        this.connections = connections;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<BankAccountDTO> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccountDTO> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }

    public List<UserConnectionDTO> getConnections() {
        return connections;
    }

    public void setConnections(List<UserConnectionDTO> connections) {
        this.connections = connections;
    }
}
