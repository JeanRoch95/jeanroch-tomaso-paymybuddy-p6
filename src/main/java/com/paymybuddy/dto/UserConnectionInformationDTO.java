package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;

public class UserConnectionInformationDTO {

    @Email
    private String email;

    private String Name;

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
}
