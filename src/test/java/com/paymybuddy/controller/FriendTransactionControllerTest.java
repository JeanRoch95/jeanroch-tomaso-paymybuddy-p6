package com.paymybuddy.controller;

import com.paymybuddy.dto.FriendTransactionCreateDTO;
import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import com.paymybuddy.service.impl.FriendTransactionServiceImpl;
import com.paymybuddy.service.impl.UserConnectionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
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
    private AccountService accountService;

    @MockBean
    private BalanceService balanceService;

    @Test
    public void testDisplayFriendTransactionPage() throws Exception {
        FriendTransactionDisplayDTO createDTO = new FriendTransactionDisplayDTO();
        List<FriendTransactionDisplayDTO> transactionContent = Collections.singletonList(new FriendTransactionDisplayDTO());
        Page<FriendTransactionDisplayDTO> transactionPage = new PageImpl<>(transactionContent);

        List<UserConnectionInformationDTO> connectionDTOS = Collections.singletonList(new UserConnectionInformationDTO());

        BigDecimal balance = BigDecimal.valueOf(1000.0);
        BigDecimal finalPrice = BigDecimal.valueOf(950.0);

        when(friendTransactionService.getTransactionsForUser(any(PageRequest.class))).thenReturn(transactionPage);
        when(accountService.getAllConnectionByCurrentAccount()).thenReturn(connectionDTOS);
        when(accountService.getCurrentUserBalance()).thenReturn(balance);
        when(balanceService.calculateMaxPrice(any(BigDecimal.class))).thenReturn(finalPrice);

        mockMvc.perform(get("/friend-money-send"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("friendTransaction", instanceOf(FriendTransactionCreateDTO.class)))
                .andExpect(model().attribute("connections", connectionDTOS))
                .andExpect(model().attribute("balance", balance))
                .andExpect(model().attribute("finalPrice", finalPrice))
                .andExpect(model().attribute("page", transactionPage))
                .andExpect(model().attribute("transactions", transactionPage.getContent()))
                .andExpect(view().name("friend_transaction"));

        verify(friendTransactionService, times(1)).getTransactionsForUser(any(PageRequest.class));
        verify(accountService, times(1)).getAllConnectionByCurrentAccount();
        verify(accountService, times(1)).getCurrentUserBalance();
        verify(balanceService, times(1)).calculateMaxPrice(any(BigDecimal.class));
    }

    @Test
    public void testSendMoneyToFriend_Success() throws Exception {
        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();

        mockMvc.perform(post("/friend-money-send")
                        .flashAttr("friendTransaction", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friend-money-send"))
                .andExpect(flash().attribute("success", "L'argent a bien été envoyé à votre ami"));

        verify(friendTransactionService, times(1)).sendMoneyToFriend(dto);
    }

    @Test
    public void testSendMoneyToFriend_InsufficientBalance() throws Exception {
        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        String errorMessage = "Fonds insuffisants";

        doThrow(new InsufficientBalanceException(errorMessage)).when(friendTransactionService).sendMoneyToFriend(dto);

        mockMvc.perform(post("/friend-money-send")
                        .flashAttr("friendTransaction", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friend-money-send"))
                .andExpect(flash().attribute("errorMessage", errorMessage));

        verify(friendTransactionService, times(1)).sendMoneyToFriend(dto);
    }

    @Test
    public void testSendMoneyToFriend_NullTransfer() throws Exception {
        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        String errorMessage = "Le montant ne peux être null ou inférieur a 0€";

        doThrow(new NullTransferException(errorMessage)).when(friendTransactionService).sendMoneyToFriend(dto);

        mockMvc.perform(post("/friend-money-send")
                        .flashAttr("friendTransaction", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friend-money-send"))
                .andExpect(flash().attribute("errorMessage", errorMessage));

        verify(friendTransactionService, times(1)).sendMoneyToFriend(dto);
    }
}


