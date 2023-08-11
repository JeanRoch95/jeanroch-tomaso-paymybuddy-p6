package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;

import java.time.Instant;

public class UserInformationDTO {

    private String firstName;

    private String lastName;

    @Email
    private String email;

    private Instant updatedAt;

    public UserInformationDTO() {
    }

    public UserInformationDTO(String firstName, String lastName, String email, Instant updatedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = updatedAt;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
