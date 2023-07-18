package com.paymybuddy.service;

import com.paymybuddy.model.Bank;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankTransferRepository bankTransferRepository;

    @Mock
    private BankService bankService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankService = new BankServiceImpl(bankRepository, userRepository, bankTransferRepository);
    }

    @Test
    public void findAllBank() {
        List<Bank> mockBank = Arrays.asList(new Bank(), new Bank());
        User user = new User();
        user.setId(1L);
        when(bankRepository.findAll()).thenReturn(mockBank);

        Iterable<Bank> result = bankService.findAllBank(1);

        assertEquals(mockBank, result);
        verify(bankRepository).findAll();

    }

    @Test
    public void testAddBank() {
        String iban = "iban";
        String swift = "swift";
        String name = "name";

        User user = new User();
        user.setId(1L);

        Bank bank = new Bank();
        bank.setIban(iban);
        bank.setSwift(swift);
        bank.setName(name);
        bank.setUser(user);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankRepository.save(any(Bank.class))).thenReturn(bank);

        Bank result = bankService.addBank(iban, swift, name);

        assertNotNull(result);
        assertEquals(bank.getIban(), result.getIban());
        assertEquals(bank.getSwift(), result.getSwift());
        assertEquals(bank.getName(), result.getName());
        assertEquals(user.getId(), result.getUser().getId());
    }
}

