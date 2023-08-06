package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;

public class UserConnectionInformationDTO {

    @Email
    private String email;

    private String Name;

    private Long receiverUserId;

    public UserConnectionInformationDTO(String email) {
        this.email = email;
    }

    public UserConnectionInformationDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }
}
