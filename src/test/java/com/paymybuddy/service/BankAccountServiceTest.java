package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.exceptions.DatabaseException;
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
import java.util.Collections;
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

        Iterable<BankAccount> result = bankAccountService.getBankAccountByCurrentUserId();

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

        BankAccountDTO bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setIban(iban);
        bankAccountDTO.setSwift(swift);
        bankAccountDTO.setName(name);
        bankAccountDTO.setUserId(user.getId());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bankAccountRepository.findBankAccountsByUserId(any(Integer.class))).thenReturn(Collections.emptyList());

        BankAccount result = bankAccountService.addBankAccount(bankAccountDTO);

        assertNotNull(result);
        assertEquals(bankAccountDTO.getIban(), result.getIban());
        assertEquals(bankAccountDTO.getSwift(), result.getSwift());
        assertEquals(bankAccountDTO.getName(), result.getName());
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    void testAddBankAccount_ibanAlreadyExists() {

        User user = new User();
        user.setId(1L);

        BankAccountDTO bankAccountDTO = new BankAccountDTO("iban", "swift", "name");

        BankAccount existingBankAccount = new BankAccount(bankAccountDTO.getIban(), bankAccountDTO.getSwift(), bankAccountDTO.getName(), user);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.findBankAccountsByUserId(anyInt())).thenReturn(Collections.singletonList(existingBankAccount));

        assertThrows(DatabaseException.class, () -> bankAccountService.addBankAccount(bankAccountDTO));
    }

    @Test
    void testAddBankAccount_databaseErrorOnSave() {
        User user = new User();
        user.setId(1L);

        BankAccountDTO bankAccountDTO = new BankAccountDTO("iban", "swift", "name");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.findBankAccountsByUserId(anyInt())).thenReturn(Collections.emptyList());
        when(bankAccountRepository.save(any(BankAccount.class))).thenThrow(RuntimeException.class);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> bankAccountService.addBankAccount(bankAccountDTO));
    }
}

