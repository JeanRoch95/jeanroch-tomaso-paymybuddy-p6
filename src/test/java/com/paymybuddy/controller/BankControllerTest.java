package com.paymybuddy.controller;

import com.paymybuddy.model.Bank;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankService;
import com.paymybuddy.service.BankServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankServiceImpl bankService;

    private Bank bank;

    private User user;

    @BeforeEach
    public void setUpBeforeEachTest() {
        user = new User();
        bank = new Bank("IBAN", "SWIFT", "NAME", user);
    }


    @Test
    public void testGetAddPage() throws Exception {
        mockMvc.perform(get("/bank_add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_add"));
    }

    @Test
    void testTransferPage() throws Exception {
        when(bankService.addBank(bank.getIban(), bank.getSwift(), bank.getName())).thenReturn(bank);

        mockMvc.perform(post("/addbank")
                        .param("bankIban", "IBAN")
                        .param("bankSwift", "SWIFT")
                        .param("bankName", "NAME"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_add_confirmation"));
    }
}
