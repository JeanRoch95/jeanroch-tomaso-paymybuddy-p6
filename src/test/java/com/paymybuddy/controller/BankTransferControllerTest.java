package com.paymybuddy.controller;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.BankTransferService;
import com.paymybuddy.service.BankTransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BankTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankTransferServiceImpl bankTransferService;

    @MockBean
    private BankAccountServiceImpl bankAccountService;

    @Test
    public void testDisplayTransferPage() throws Exception {
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        Page<TransactionDTO> transactionPage = new PageImpl<>(transactionDTOList, pageRequest, transactionDTOList.size());
        List<BankAccount> bankList = new ArrayList<>();
        Double balance = 100.0;

        when(bankTransferService.getTransferDetails(any(Pageable.class))).thenReturn(transactionPage);
        when(bankAccountService.getBankAccountByCurrentUserId()).thenReturn(bankList);
        when(bankTransferService.getUserBalance()).thenReturn(balance);

        mockMvc.perform(get("/bank-money-send")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bankTransfer", instanceOf(BankTransferDTO.class)))
                .andExpect(model().attribute("banklist", bankList))
                .andExpect(model().attribute("balance", balance))
                .andExpect(model().attribute("page", transactionPage))
                .andExpect(model().attribute("transactions", transactionDTOList))
                .andExpect(view().name("bank_transfer"));

        verify(bankTransferService).getTransferDetails(any(Pageable.class));
        verify(bankAccountService).getBankAccountByCurrentUserId();
        verify(bankTransferService).getUserBalance();
    }

    @Test
    public void testSendMoney_WithInsufficientBalanceException() throws Exception {
        BankTransferDTO bankTransferDTO = new BankTransferDTO();
        String action = "Envoyer";

        doThrow(new InsufficientBalanceException("Solde insuffisant pour le transfer."))
                .when(bankTransferService)
                .debitFromBankAccount(any(BankTransferDTO.class));

        mockMvc.perform(post("/bank-money-send")
                        .param("action", action)
                        .flashAttr("bankTransfer", bankTransferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(bankTransferService).debitFromBankAccount(bankTransferDTO);
    }

    @Test
    public void testSendMoney_WithNullTransferException() throws Exception {
        BankTransferDTO bankTransferDTO = new BankTransferDTO();
        String action = "Envoyer";

        doThrow(new NullTransferException("Le montant de la transaction ne doit pas être nul"))
                .when(bankTransferService)
                .debitFromBankAccount(any(BankTransferDTO.class));

        mockMvc.perform(post("/bank-money-send")
                        .param("action", action)
                        .flashAttr("bankTransfer", bankTransferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(bankTransferService).debitFromBankAccount(bankTransferDTO);
    }

    @Test
    public void testSendMoney_debitFromBank() throws Exception {
        BankTransferDTO bankTransferDTO = new BankTransferDTO();
        String action = "Envoyer";

        mockMvc.perform(post("/bank-money-send")
                        .param("action", action)
                        .flashAttr("bankTransfer", bankTransferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("success"));

        verify(bankTransferService).debitFromBankAccount(bankTransferDTO);
    }

    @Test
    public void testSendMoney_creditFromBank() throws Exception {
        BankTransferDTO bankTransferDTO = new BankTransferDTO();
        String action = "Recevoir";

        mockMvc.perform(post("/bank-money-send")
                        .param("action", action)
                        .flashAttr("bankTransfer", bankTransferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("success"));

        verify(bankTransferService).creditFromBankAccount(bankTransferDTO);
    }
}
