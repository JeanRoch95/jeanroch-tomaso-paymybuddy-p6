package com.paymybuddy.service;

import com.paymybuddy.constant.Fee;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.BalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class BalanceServiceTest {

    @Mock
    private BalanceService balanceService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        balanceService = new BalanceServiceImpl(userRepository, accountService);
    }
    @Test
    void calculateFinalPriceTest() {
        BigDecimal amount = new BigDecimal("100");
        BigDecimal expectedFinalPrice = amount.add(amount.multiply(Fee.FRIEND_TRANSACTION_FEES));

        BigDecimal result = balanceService.calculateFinalPrice(amount);

        assertEquals(expectedFinalPrice, result, "Expected final price should match the calculated one.");
    }

    @Test
    void calculateMaxPriceTest_balanceNull() {
        BigDecimal balance = null;
        BigDecimal expectedMaxPrice = BigDecimal.valueOf(0.0);

        BigDecimal result = balanceService.calculateMaxPrice(balance);

        assertEquals(expectedMaxPrice, result, "Expected max price should be 0 if balance is null.");
    }

    @Test
    void calculateMaxPriceTest_withValidBalance() {
        BigDecimal balance = new BigDecimal("100");
        BigDecimal expectedMaxPrice = balance.subtract(balance.multiply(Fee.FRIEND_TRANSACTION_FEES));

        BigDecimal result = balanceService.calculateMaxPrice(balance);

        assertEquals(expectedMaxPrice, result, "Expected max price should match the calculated one.");
    }

    @Test
    void getCurrentUserBalance_UserFound() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);
        BigDecimal expectedBalance = BigDecimal.valueOf(100.50);
        User mockUser = new User();
        mockUser.setBalance(expectedBalance);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userRepository.findById(mockUserDTO.getId().intValue())).thenReturn(Optional.of(mockUser));

        BigDecimal result = balanceService.getCurrentUserBalance();

        assertEquals(expectedBalance, result);
    }

    @Test
    void getCurrentUserBalance_UserNotFound() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userRepository.findById(mockUserDTO.getId().intValue())).thenReturn(Optional.empty());

        BigDecimal result = balanceService.getCurrentUserBalance();

        assertEquals(BigDecimal.valueOf(0.0), result);
    }
}
