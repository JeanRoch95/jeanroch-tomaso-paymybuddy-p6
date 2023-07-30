package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.exceptions.DatabaseException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
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

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankAccountService = new BankAccountServiceImpl(bankAccountRepository, userRepository);
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
    void testAddBankAccount_userNotFound() {
        BankAccountDTO bankAccountDTO = new BankAccountDTO("iban", "swift", "name");
        bankAccountDTO.setUserId(1L);

        when(userRepository.findById(bankAccountDTO.getUserId().intValue())).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseException.class, () -> bankAccountService.addBankAccount(bankAccountDTO));
        assertEquals("User does not exist in the database", exception.getMessage());
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

            when(bankAccountRepository.findBankAccountsByUserId(anyInt(), eq(pageRequest))).thenReturn(bankAccountsPage);

            Page<BankAccount> result = bankAccountService.getSortedBankAccountByCurrentUserId(pageRequest);

            assertNotNull(result);
            assertEquals(bankAccountsPage, result);
        }
    }

    @Test
    public void testGetBankAccountByIbanAndUserId() {
        User user = new User();
        user.setId(1L);
        String iban = "iban";
        BankAccount account1 = new BankAccount(iban, "swift", "name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(iban, user.getId().intValue())).thenReturn(account1);

        BankAccount result = bankAccountService.getBankAccountByIbanAndUserId(iban);

        assertNotNull(result);
        assertEquals(account1, result);
        verify(bankAccountRepository).findByIbanAndUser_Id(iban, user.getId().intValue());
    }



}

