package com.paymybuddy.controller;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.BankTransferService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import com.paymybuddy.service.impl.BankTransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    private BankTransferService bankTransferService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private BalanceService balanceService;

    @Test
    public void testDisplayTransferPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankTransferInformationDTO> page = new PageImpl<>(Collections.emptyList());
        when(bankTransferService.getTransferDetails(any(Pageable.class))).thenReturn(page);
        when(accountService.getBankAccountByCurrentUserId()).thenReturn(Collections.emptyList());
        when(balanceService.getCurrentUserBalance()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/bank-money-send")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank_transfer"))
                .andExpect(model().attribute("banklist", Collections.emptyList()))
                .andExpect(model().attribute("balance", BigDecimal.ZERO))
                .andExpect(model().attribute("hasTransfers", false))
                .andExpect(model().attribute("page", page))
                .andExpect(model().attribute("transactions", Collections.emptyList()));
    }

    @Test
    public void sendMoney_Success_ShouldRedirectWithSuccessMessage() throws Exception {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setAmount(BigDecimal.valueOf(100));
        bankTransferCreateDTO.setIban("IBAN 1234 1234 1234 1234");
        bankTransferCreateDTO.setType(TransactionTypeEnum.TransactionType.CREDIT);


        mockMvc.perform(post("/bank-money-send")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iban", bankTransferCreateDTO.getIban())
                        .param("type", bankTransferCreateDTO.getType().toString())
                        .param("amount", bankTransferCreateDTO.getAmount().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attribute("success", "L'argent a bien été transféré"));

        verify(bankTransferService, times(1)).processBankTransfer(any());
    }

    @Test
    public void sendMoney_InvalidAmount_ShouldRedirectWithErrorMessage() throws Exception {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setAmount(BigDecimal.valueOf(-100));
        bankTransferCreateDTO.setIban("IBAN 1234 1234 1234 1234");
        bankTransferCreateDTO.setType(TransactionTypeEnum.TransactionType.CREDIT);

        doThrow(new InvalidAmountException("Le montant n'est pas valide."))
                .when(bankTransferService)
                .processBankTransfer(any(BankTransferCreateDTO.class));

        mockMvc.perform(post("/bank-money-send")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iban", bankTransferCreateDTO.getIban())
                        .param("type", bankTransferCreateDTO.getType().toString())
                        .param("amount", bankTransferCreateDTO.getAmount().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attribute("errorMessage", "Le montant n'est pas valide."));

        verify(bankTransferService, times(1)).processBankTransfer(any());
    }
}
