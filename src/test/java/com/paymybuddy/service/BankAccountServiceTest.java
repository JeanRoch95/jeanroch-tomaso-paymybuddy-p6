package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountCreateDTO;
import com.paymybuddy.exceptions.DatabaseException;
import com.paymybuddy.exceptions.IbanAlreadyExistsException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.BankAccountMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    private BankAccountService bankAccountService;

    @Mock
    BankAccountMapper bankAccountMapper;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankAccountService = new BankAccountServiceImpl(bankAccountRepository, userRepository, bankAccountMapper);
    }

    @Test
    public void getAllBanksAccountByUserId() {
        User user = new User();
        user.setId(1L);
        BankAccount bankAccount1 = new BankAccount("iban1", "swift1", "name1", user);
        BankAccount bankAccount2 = new BankAccount("iban2", "swift2", "name2", user);
        List<BankAccount> bankAccounts = Arrays.asList(bankAccount1, bankAccount2);

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(1);
            when(bankAccountRepository.findByUserId(1)).thenReturn(bankAccounts);

            Iterable<BankAccountDTO> result = bankAccountService.getBankAccountByCurrentUserId();
            List<BankAccountDTO> resultAsList = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(bankAccounts.size(), resultAsList.size());
            assertEquals(bankAccount1.getIban(), resultAsList.get(0).getIban());
            assertEquals(bankAccount2.getIban(), resultAsList.get(1).getIban());

            verify(bankAccountRepository).findByUserId(1);
        }
    }

    @Test
    void testAddBankAccount_Success() {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO("name", "iban", "swift");
        User user = new User();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.findByUserId(anyInt())).thenReturn(Arrays.asList());
        when(bankAccountMapper.toDTO(any())).thenReturn(new BankAccountDTO());

        BankAccountDTO result = bankAccountService.addBankAccount(bankAccountCreateDTO);

        assertNotNull(result);
        verify(bankAccountRepository).save(any());
    }


    @Test
    void testAddBankAccount_userNotFound() {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO("iban", "swift", "name");
        bankAccountCreateDTO.setUserId(1L);

        when(userRepository.findById(bankAccountCreateDTO.getUserId().intValue())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> bankAccountService.addBankAccount(bankAccountCreateDTO));
        assertEquals("Erreur 404 - BAD REQUEST", exception.getMessage());
    }

    @Test
    void testAddBankAccount_ibanAlreadyExists() {

        User user = new User();
        user.setId(1L);

        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO("iban", "swift", "name");

        BankAccount existingBankAccount = new BankAccount(bankAccountCreateDTO.getIban(), bankAccountCreateDTO.getSwift(), bankAccountCreateDTO.getName(), user);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.findByUserId(anyInt())).thenReturn(Collections.singletonList(existingBankAccount));

        assertThrows(IbanAlreadyExistsException.class, () -> bankAccountService.addBankAccount(bankAccountCreateDTO));
    }

    @Test
    void testAddBankAccount_databaseErrorOnSave() {
        User user = new User();
        user.setId(1L);

        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO("iban", "swift", "name");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bankAccountRepository.findByUserId(anyInt())).thenReturn(Collections.emptyList());
        when(bankAccountRepository.save(any(BankAccount.class))).thenThrow(RuntimeException.class);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> bankAccountService.addBankAccount(bankAccountCreateDTO));
    }

    @Test
    public void testGetSortedBankAccountByCurrentUserId() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(1);

            User user = new User();
            user.setId(1L);
            BankAccount account1 = new BankAccount("iban1", "swift1", "name1", user);
            BankAccount account2 = new BankAccount("iban2", "swift2", "name2", user);

            List<BankAccount> bankAccounts = Arrays.asList(account1, account2);
            Page<BankAccount> bankAccountsPage = new PageImpl<>(bankAccounts);

            PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("createdAt").descending());

            when(bankAccountRepository.findByUserIdOrderByCreatedAtDesc(anyInt(), eq(pageRequest))).thenReturn(bankAccountsPage);

            Page<BankAccountDTO> result = bankAccountService.getSortedBankAccountByCurrentUserId(pageRequest);

            assertNotNull(result);
            assertEquals(bankAccounts.size(), result.getContent().size());
            assertEquals(account1.getIban(), result.getContent().get(0).getIban());
            assertEquals(account2.getIban(), result.getContent().get(1).getIban());

    }
}
}

