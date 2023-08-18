package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountCreateDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.exceptions.DatabaseException;
import com.paymybuddy.exceptions.IbanAlreadyExistsException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.BankAccountMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
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
    private BankAccountMapper bankAccountMapper;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankAccountService = new BankAccountServiceImpl(bankAccountRepository, userRepository, bankAccountMapper, accountService);
    }

    @Test
    void testAddBankAccount_Success() {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO();
        bankAccountCreateDTO.setIban("testIban");
        bankAccountCreateDTO.setSwift("swiftTest");
        bankAccountCreateDTO.setName("nameTest");

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        User mockUser = new User();
        mockUser.setId(mockUserDTO.getId());

        BankAccount savedBankAccount = new BankAccount(bankAccountCreateDTO.getIban(), bankAccountCreateDTO.getSwift(), bankAccountCreateDTO.getName(), mockUser);
        savedBankAccount.setId(123L);

        BankAccountDTO expectedDTO = new BankAccountDTO(savedBankAccount);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userRepository.findById(mockUserDTO.getId().intValue())).thenReturn(Optional.of(mockUser));
        when(bankAccountRepository.findByUserId(mockUserDTO.getId().intValue())).thenReturn(Collections.emptyList()); // Pas de comptes existants avec cet IBAN
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savedBankAccount);
        when(bankAccountMapper.toDTO(savedBankAccount)).thenReturn(expectedDTO);

        BankAccountDTO resultDTO = bankAccountService.addBankAccount(bankAccountCreateDTO);

        assertEquals(expectedDTO, resultDTO);

        verify(userRepository).findById(anyInt());
        verify(bankAccountRepository).findByUserId(anyInt());
        verify(bankAccountRepository).save(any(BankAccount.class));
        verify(bankAccountMapper).toDTO(savedBankAccount);
    }




    @Test
    void testAddBankAccount_userNotFound() {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO("iban", "swift", "name");
        bankAccountCreateDTO.setUserId(1L);

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userRepository.findById(bankAccountCreateDTO.getUserId().intValue())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> bankAccountService.addBankAccount(bankAccountCreateDTO));
        assertEquals("Erreur 404 - BAD REQUEST", exception.getMessage());
    }

    @Test
    void testAddBankAccount_IbanAlreadyExists() {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO();
        bankAccountCreateDTO.setIban("existingIban");
        bankAccountCreateDTO.setSwift("swiftTest");
        bankAccountCreateDTO.setName("nameTest");

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        User mockUser = new User();
        mockUser.setId(mockUserDTO.getId());

        BankAccount existingBankAccount = new BankAccount("existingIban", "swiftTest", "nameTest", mockUser);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userRepository.findById(mockUserDTO.getId().intValue())).thenReturn(Optional.of(mockUser));
        when(bankAccountRepository.findByUserId(mockUserDTO.getId().intValue())).thenReturn(Arrays.asList(existingBankAccount));

        assertThrows(IbanAlreadyExistsException.class, () -> bankAccountService.addBankAccount(bankAccountCreateDTO));

        verify(userRepository).findById(anyInt());
        verify(bankAccountRepository).findByUserId(anyInt());
        verify(bankAccountRepository, never()).save(any(BankAccount.class)); // s'assurer qu'aucun compte n'a été enregistré
    }


    @Test
    void testAddBankAccount_DatabaseException() {
        BankAccountCreateDTO bankAccountCreateDTO = new BankAccountCreateDTO();
        bankAccountCreateDTO.setIban("testIban");
        bankAccountCreateDTO.setSwift("swiftTest");
        bankAccountCreateDTO.setName("nameTest");

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        User mockUser = new User();
        mockUser.setId(mockUserDTO.getId());

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userRepository.findById(mockUserDTO.getId().intValue())).thenReturn(Optional.of(mockUser));
        when(bankAccountRepository.findByUserId(mockUserDTO.getId().intValue())).thenReturn(Collections.emptyList()); // Pas de comptes existants avec cet IBAN

        when(bankAccountRepository.save(any(BankAccount.class))).thenThrow(new RuntimeException("Erreur lors de la sauvegarde"));

        assertThrows(DatabaseException.class, () -> bankAccountService.addBankAccount(bankAccountCreateDTO));

        verify(userRepository).findById(anyInt());
        verify(bankAccountRepository).findByUserId(anyInt());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }


    @Test
    public void testGetSortedBankAccountByCurrentUserId() {
        User user = new User();
        user.setId(1L);
        BankAccount account1 = new BankAccount("iban1", "swift1", "name1", user);
        BankAccount account2 = new BankAccount("iban2", "swift2", "name2", user);

        List<BankAccount> bankAccounts = Arrays.asList(account1, account2);
        Page<BankAccount> bankAccountsPage = new PageImpl<>(bankAccounts);

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(bankAccountRepository.findByUserIdOrderByCreatedAtDesc(anyInt(), eq(pageRequest))).thenReturn(bankAccountsPage);

        Page<BankAccountDTO> result = bankAccountService.getSortedBankAccountByCurrentUserId(pageRequest);

        assertNotNull(result);
        assertEquals(bankAccounts.size(), result.getContent().size());
        assertEquals(account1.getIban(), result.getContent().get(0).getIban());
        assertEquals(account2.getIban(), result.getContent().get(1).getIban());
    }

}

