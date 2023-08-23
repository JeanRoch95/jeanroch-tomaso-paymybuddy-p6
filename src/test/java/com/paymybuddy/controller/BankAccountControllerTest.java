package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountCreateDTO;
import com.paymybuddy.exceptions.IbanAlreadyExistsException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
@SpringBootTest
@AutoConfigureMockMvc
public class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountServiceImpl bankAccountService;

    private BankAccount bankAccount;

    private User user;

    @BeforeEach
    public void setUpBeforeEachTest() {
        user = new User();
        bankAccount = new BankAccount("IBANTEST122323234567", "SWIFTTEST1", "NAME", user);
    }


    @Test
    public void testDisplayBankAccountAddPage() throws Exception {
        mockMvc.perform(get("/bank-account-add")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_account_add"))
                .andExpect(model().attributeExists("bankAccount"));
    }

    @Test
    public void addBankAccount_ValidAccount() throws Exception {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO();
        bankAccountCreateDTO.setIban("FR14 1234 1234 1234 1234 1234 123");
        bankAccountCreateDTO.setSwift("DEMOFRXX");
        bankAccountCreateDTO.setName("Compte");

        mockMvc.perform(post("/bank-account-add")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .param("iban", bankAccountCreateDTO.getIban())
                        .param("swift", bankAccountCreateDTO.getSwift())
                        .param("name", bankAccountCreateDTO.getName())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));
    }

    @Test
    public void addBankAccount_MissingCsrfToken_ShouldReturnForbidden() throws Exception {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO();
        bankAccountCreateDTO.setIban("FR14 1234 1234 1234 1234 1234 123");
        bankAccountCreateDTO.setSwift("DEMOFRXX");
        bankAccountCreateDTO.setName("Compte");

        mockMvc.perform(post("/bank-account-add")
                                .with(SecurityMockMvcRequestPostProcessors.user("user@mail.com").password("password"))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void addBankAccount_IbanAlreadyExists_ShouldRedirectWithError() throws Exception {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO();
        bankAccountCreateDTO.setIban("FR14 1234 1234 1234 1234 1234 123");
        bankAccountCreateDTO.setSwift("DEMOFRXX");
        bankAccountCreateDTO.setName("Compte");

        when(bankAccountService.addBankAccount(any(BankAccountCreateDTO.class))).thenThrow(new IbanAlreadyExistsException("L'IBAN existe déjà"));
        mockMvc.perform(post("/bank-account-add")
                        .with(SecurityMockMvcRequestPostProcessors.user("JR@mail.com").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iban", bankAccountCreateDTO.getIban())
                        .param("swift", bankAccountCreateDTO.getSwift())
                        .param("name", bankAccountCreateDTO.getName())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-account-add"))
                .andExpect(flash().attribute("errorMessage", "L'IBAN existe déjà"));
    }
}
