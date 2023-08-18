package com.paymybuddy.dto;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordDTO {

    @NotBlank(message = "Veuillez entrer le mot de passe actuel")
    private String currentPassword;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre.")
    private String newPassword;

    @NotBlank
    @Transient
    private String confirmNewPassword;

    public PasswordDTO() {

    }

    @AssertTrue(message = "Les mots de passe doivent correspondre.")
    public boolean isValid() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    public PasswordDTO(String currentPassword, String newPassword, String confirmNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
