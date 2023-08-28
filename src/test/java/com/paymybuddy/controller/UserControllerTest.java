package com.paymybuddy.controller;

import com.paymybuddy.dto.*;
import com.paymybuddy.exceptions.UserAlreadyExistException;
import com.paymybuddy.exceptions.WrongPasswordException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import com.paymybuddy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private BankAccountServiceImpl bankAccountService;

    @MockBean
    private AccountService accountService;

    @Test
    public void profilPage_ShouldDisplayProfileWithBanks() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<BankAccountDTO> bankList = new PageImpl<>(Collections.singletonList(new BankAccountDTO()));
        UserInformationDTO userInformationDTO = new UserInformationDTO();

        when(bankAccountService.getSortedBankAccountByCurrentUserId(any(Pageable.class))).thenReturn(bankList);
        when(accountService.getCurrentAccountInformations()).thenReturn(userInformationDTO);

        mockMvc.perform(get("/profil")
                .with(SecurityMockMvcRequestPostProcessors.user("username").password("password")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("hasBanks", true))
                .andExpect(model().attribute("user", userInformationDTO))
                .andExpect(model().attribute("page", bankList))
                .andExpect(model().attribute("banks", bankList.getContent()))
                .andExpect(view().name("/profil"));

        verify(bankAccountService, times(1)).getSortedBankAccountByCurrentUserId(pageable);
        verify(accountService, times(1)).getCurrentAccountInformations();
    }

    @Test
    public void profilUpdatePage_ShouldDisplayProfileUpdate() throws Exception {
        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName("John");
        userInformationDTO.setLastName("Doe");
        userInformationDTO.setEmail("John@mail.com");
        userInformationDTO.setUpdatedAt(Instant.now());

        when(accountService.getCurrentAccountInformations()).thenReturn(userInformationDTO);

        mockMvc.perform(get("/profil-update")
                .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                .param("firstName", userInformationDTO.getFirstName())
                .param("lastName", userInformationDTO.getLastName())
                .param("email", userInformationDTO.getEmail())
                .param("updatedAt", userInformationDTO.getUpdatedAt().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/profil_modify"));
    }

    @Test
    public void updateProfile_EmailChanged_ShouldRedirectToLogin() throws Exception {
        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName("John");
        userInformationDTO.setLastName("Doe");
        userInformationDTO.setEmail("John@mail.com");
        userInformationDTO.setUpdatedAt(Instant.now());

        when(accountService.checkIfEmailChanged(any())).thenReturn(true);

        mockMvc.perform(post("/profil-update")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", userInformationDTO.getFirstName())
                        .param("lastName", userInformationDTO.getLastName())
                        .param("email", userInformationDTO.getEmail())
                        .param("updatedAt", userInformationDTO.getUpdatedAt().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("infoMessage", "Votre e-mail a été modifié. Veuillez vous reconnecter."));

        verify(accountService, times(1)).updateCurrentAccountInformation(any());
    }

    @Test
    public void updateProfile_EmailNotChanged_ShouldRedirectToProfile() throws Exception {
        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName("John");
        userInformationDTO.setLastName("Doe");
        userInformationDTO.setEmail("John@mail.com");
        userInformationDTO.setUpdatedAt(Instant.now());

        when(accountService.checkIfEmailChanged(any())).thenReturn(false);

        mockMvc.perform(post("/profil-update")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", userInformationDTO.getFirstName())
                        .param("lastName", userInformationDTO.getLastName())
                        .param("email", userInformationDTO.getEmail())
                        .param("updatedAt", userInformationDTO.getUpdatedAt().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attribute("successMessage", "Vos informations ont été mises à jour avec succès!"));

        verify(accountService, times(1)).updateCurrentAccountInformation(any());
    }


    @Test
    public void processRegistration_Success_ShouldRedirectToLogin() throws Exception {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("John");
        userCreateDTO.setLastName("Doe");
        userCreateDTO.setPassword("Xxxxxxxxx1");
        userCreateDTO.setConfirmPassword("Xxxxxxxxx1");
        userCreateDTO.setEmail("John@mail.com");
        userCreateDTO.setCreatedAt(Instant.now());

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", userCreateDTO.getFirstName())
                        .param("lastName", userCreateDTO.getLastName())
                        .param("email", userCreateDTO.getEmail())
                        .param("createdAt", userCreateDTO.getCreatedAt().toString())
                        .param("password", userCreateDTO.getPassword())
                        .param("confirmPassword", userCreateDTO.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("success", "Votre compte a bien été crée."));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    public void processChangePassword_Success_ShouldRedirectToProfile() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setCurrentPassword("Xxxxxxxx1");
        passwordDTO.setNewPassword("Xxxxxxxx2");
        passwordDTO.setConfirmNewPassword("Xxxxxxxx2");

        mockMvc.perform(post("/update-password")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("currentPassword", passwordDTO.getCurrentPassword())
                        .param("newPassword", passwordDTO.getNewPassword())
                        .param("confirmNewPassword", passwordDTO.getConfirmNewPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attribute("successMessage", "Mot de passe modifié avec success"));

        verify(accountService, times(1)).updateCurrentAccountPassword(any());
    }

    @Test
    public void showRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("register"));
    }

    @Test
    public void processRegistration_UserAlreadyExists_ShouldRedirectToRegisterWithErrorMessage() throws Exception {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("John");
        userCreateDTO.setLastName("Doe");
        userCreateDTO.setPassword("Xxxxxxxxx1");
        userCreateDTO.setConfirmPassword("Xxxxxxxxx1");
        userCreateDTO.setEmail("John@mail.com");

        doThrow(new UserAlreadyExistException("Utilisateur déjà existant."))
                .when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", userCreateDTO.getFirstName())
                        .param("lastName", userCreateDTO.getLastName())
                        .param("email", userCreateDTO.getEmail())
                        .param("password", userCreateDTO.getPassword())
                        .param("confirmPassword", userCreateDTO.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "Utilisateur déjà existant."));

        verify(userService, times(1)).createUser(any());
    }




}
