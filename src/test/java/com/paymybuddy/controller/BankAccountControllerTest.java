package com.paymybuddy.controller;

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
    private BankAccountServiceImpl bankService;

    private BankAccount bankAccount;

    private User user;

    @BeforeEach
    public void setUpBeforeEachTest() {
        user = new User();
        bankAccount = new BankAccount("IBANTEST122323234567", "SWIFTTEST1", "NAME", user);
    }


    @Test
    public void testGetAddPage() throws Exception {
        mockMvc.perform(get("/bank_add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_account_add"));
    }

    @Test
    void testTransferPage() throws Exception {
        when(bankService.addBank(bankAccount.getIban(), bankAccount.getSwift(), bankAccount.getName())).thenReturn(bankAccount);

        mockMvc.perform(post("/addbank")
                        .param("bankIban", "IBANTEST122323234567")
                        .param("bankSwift", "SWIFTTEST1")
                        .param("bankName", "NAME"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_add_confirmation"));
    }
}
