package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferDisplayDTO;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
        mockMvc.perform(get("/bank-account-add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_account_add"));
    }

    @Test
    void testAddBankAccount() throws Exception {
        BankAccountDTO bankAccountDTO = new BankAccountDTO("ibanValid123456", "swiftValid", "name");
        when(bankAccountService.addBankAccount(any(BankTransferDisplayDTO.class))).thenReturn(bankAccount);

        mockMvc.perform(post("/bank-account-add")
                        .param("iban", bankAccountDTO.getIban())
                        .param("swift", bankAccountDTO.getSwift())
                        .param("name", bankAccountDTO.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));
    }

    @Test
    void testAddBankAccount_IfEnterWrongArgument() throws Exception {
        BankAccountDTO bankAccountDTO = new BankAccountDTO("invalidIban", "swiftValid", "name");
        when(bankAccountService.addBankAccount(any(BankTransferDisplayDTO.class))).thenReturn(bankAccount);

        mockMvc.perform(post("/bank-account-add")
                        .param("iban", bankAccountDTO.getIban())
                        .param("swift", bankAccountDTO.getSwift())
                        .param("name", bankAccountDTO.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_account_add"));
    }

    @Test
    void testAddBankAccount_IfEnterNoArgument() throws Exception {
        BankAccountDTO bankAccountDTO = new BankAccountDTO("", "", "");
        when(bankAccountService.addBankAccount(any(BankTransferDisplayDTO.class))).thenReturn(bankAccount);

        mockMvc.perform(post("/bank-account-add")
                        .param("iban", bankAccountDTO.getIban())
                        .param("swift", bankAccountDTO.getSwift())
                        .param("name", bankAccountDTO.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_account_add"));
    }
}
