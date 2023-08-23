package com.paymybuddy.controller;

import com.paymybuddy.dto.FriendTransactionCreateDTO;
import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import com.paymybuddy.service.UserConnectionService;
import com.paymybuddy.service.impl.FriendTransactionServiceImpl;
import com.paymybuddy.service.impl.UserConnectionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class FriendTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendTransactionServiceImpl friendTransactionService;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private UserConnectionService userConnectionService;

    @Test
    public void displayFriendTransactionPage_ShouldDisplayTransactions() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<FriendTransactionDisplayDTO> transactionPage = new PageImpl<>(Collections.emptyList());

        when(friendTransactionService.getTransactionsForUser(any(PageRequest.class))).thenReturn(transactionPage);
        when(userConnectionService.getAllConnectionByCurrentAccount()).thenReturn(Collections.emptyList());
        when(balanceService.getCurrentUserBalance()).thenReturn(BigDecimal.ZERO);
        when(balanceService.calculateMaxPrice(any())).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/friend-money-send")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("friend_transaction"));
    }


    @Test
    public void sendMoneyToFriend_Success_ShouldRedirectWithSuccessMessage() throws Exception {
        FriendTransactionCreateDTO friendTransactionCreateDTO = new FriendTransactionCreateDTO();
        friendTransactionCreateDTO.setAmount(BigDecimal.valueOf(100));
        friendTransactionCreateDTO.setReceiverUserId(1L);

        mockMvc.perform(post("/friend-money-send")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", friendTransactionCreateDTO.getAmount().toString())
                        .param("receiverId", friendTransactionCreateDTO.getReceiverUserId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friend-money-send"))
                .andExpect(flash().attribute("success", "L'argent a bien été envoyé à votre ami"));

        verify(friendTransactionService, times(1)).sendMoneyToFriend(any());
    }

    @Test
    public void sendMoneyToFriend_InvalidAmount_ShouldRedirectWithErrorMessage() throws Exception {
        FriendTransactionCreateDTO friendTransactionCreateDTO = new FriendTransactionCreateDTO();
        friendTransactionCreateDTO.setReceiverUserId(1L);
        friendTransactionCreateDTO.setAmount(BigDecimal.valueOf(-100));

        doThrow(new InvalidAmountException("Le montant n'est pas valide."))
                .when(friendTransactionService)
                .sendMoneyToFriend(any(FriendTransactionCreateDTO.class));

        mockMvc.perform(post("/friend-money-send")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", friendTransactionCreateDTO.getAmount().toString())
                        .param("receiverId", friendTransactionCreateDTO.getReceiverUserId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friend-money-send"))
                .andExpect(flash().attribute("errorMessage", "Le montant n'est pas valide."));

        verify(friendTransactionService, times(1)).sendMoneyToFriend(any());
    }
}


