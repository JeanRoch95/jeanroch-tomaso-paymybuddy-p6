package com.paymybuddy.service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
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
public class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankTransferRepository bankTransferRepository;

    @Mock
    private BankAccountService bankAccountService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankAccountService = new BankAccountServiceImpl(bankAccountRepository, userRepository, bankTransferRepository);
    }

    @Test
    public void getAllBanksAccountByUserId() {
        List<BankAccount> mockBankAccount = Arrays.asList(new BankAccount(), new BankAccount());
        User user = new User();
        user.setId(1L);
        when(bankAccountRepository.findBankAccountsByUserId(any(Integer.class))).thenReturn(mockBankAccount);

        Iterable<BankAccount> result = bankAccountService.getBankAccountByUserId(1);

        assertEquals(mockBankAccount, result);
        verify(bankAccountRepository).findBankAccountsByUserId(1);

    }

    @Test
    public void testAddBankAccount() {
        String iban = "iban";
        String swift = "swift";
        String name = "name";

        User user = new User();
        user.setId(1L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(iban);
        bankAccount.setSwift(swift);
        bankAccount.setName(name);
        bankAccount.setUser(user);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount result = bankAccountService.addBank(iban, swift, name);

        assertNotNull(result);
        assertEquals(bankAccount.getIban(), result.getIban());
        assertEquals(bankAccount.getSwift(), result.getSwift());
        assertEquals(bankAccount.getName(), result.getName());
        assertEquals(user.getId(), result.getUser().getId());
    }
}

