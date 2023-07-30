package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.TransactionMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class BankTransferServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankTransferRepository bankTransferRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private BankTransferService bankTransferService;

    private TransactionMapper mapper;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankTransferService = new BankTransferServiceImpl(userRepository, bankAccountRepository, bankTransferRepository, bankAccountService);
        mapper = Mappers.getMapper(TransactionMapper.class);

    }

    @Test
    public void testGetUserBalance() {
        User userTest = new User();
        userTest.setBalance(12.3);
        userTest.setId(1L);

        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(userTest));

        Double result = bankTransferService.getUserBalance();

        assertNotNull(result);
        assertEquals(userTest.getBalance(), result);
    }

    @Test
    public void testCreditFromBankAccount() {
        BankTransferDTO bankTransferDTO = new BankTransferDTO("Iban", "descriptionTest", 100.0);

        User user = new User();
        user.setId(1L);
        user.setBalance(100.0);

        BankAccount bankAccount = new BankAccount("Iban", "Swift", "Name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(bankTransferDTO.getIban(), user.getId().intValue())).thenReturn(bankAccount);

        when(bankTransferRepository.save(any(BankTransfer.class))).thenAnswer(i -> i.getArguments()[0]);

        bankTransferService.creditFromBankAccount(bankTransferDTO);

        assertEquals(user.getBalance(), 200.0, 0.01);
        verify(bankTransferRepository).save(any(BankTransfer.class));
    }

    @Test
    public void testDebitFromBankAccount() {
        BankTransferDTO bankTransferDTO = new BankTransferDTO("Iban", "descriptionTest", 200.0);

        User user = new User();
        user.setId(1L);
        user.setBalance(300.0);

        BankAccount bankAccount = new BankAccount("Iban", "Swift", "Name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(bankTransferDTO.getIban(), user.getId().intValue())).thenReturn(bankAccount);

        when(bankTransferRepository.save(any(BankTransfer.class))).thenAnswer(i -> i.getArguments()[0]);

        bankTransferService.debitFromBankAccount(bankTransferDTO);

        assertEquals(user.getBalance(), 100.0, 0.01);
        verify(bankTransferRepository).save(any(BankTransfer.class));
    }

    @Test
    void testDebitFromBankAccount_insufficientBalance() {
        BankTransferDTO bankTransferDTO = new BankTransferDTO("Iban", "descriptionTest", 200.0);


        User user = new User();
        user.setId(1L);
        user.setBalance(100.00);

        BankAccount bankAccount = new BankAccount("iban", "swift", "name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(bankTransferDTO.getIban(), user.getId().intValue())).thenReturn(bankAccount);

        Exception exception = assertThrows(InsufficientBalanceException.class, () -> bankTransferService.debitFromBankAccount(bankTransferDTO));
        assertEquals("Solde insuffisant pour le transfer.", exception.getMessage());
    }

    @Test
    void testDebitFromBankAccount_negativeAmount() {
        BankTransferDTO bankTransferDTO = new BankTransferDTO();
        bankTransferDTO.setAmount(-100.00);
        bankTransferDTO.setIban("iban");
        bankTransferDTO.setDescription("description");

        User user = new User();
        user.setId(1L);
        user.setBalance(1000.00);

        BankAccount bankAccount = new BankAccount("iban", "swift", "name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(bankTransferDTO.getIban(), user.getId().intValue())).thenReturn(bankAccount);

        Exception exception = assertThrows(NullTransferException.class, () -> bankTransferService.debitFromBankAccount(bankTransferDTO));
        assertEquals("Le montant de la transaction ne doit pas être nul", exception.getMessage());
    }

    @Test
    public void testGetTransferForUser() {
        User user = new User();
        user.setId(1L);

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setId(1L);
        bankTransfer.setBankAccount(new BankAccount("Iban", "Swift", "Name", user));

        List<BankTransfer> bankTransferList = Arrays.asList(bankTransfer);
        Page<BankTransfer> page = new PageImpl<>(bankTransferList);
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(user.getId().intValue())).thenReturn(Optional.of(user));
        when(bankTransferRepository.findByBankAccount_User(user, pageable)).thenReturn(page);

        Page<BankTransfer> result = bankTransferService.getTransferForUser(pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(bankTransfer, result.getContent().get(0));
    }

    @Test
    public void testGetTransferForUser_userNotFound() {
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> bankTransferService.getTransferForUser(pageable));
        assertEquals("Utilisateur inexistant", exception.getMessage());
    }

    @Test
    void testGetTransferDetails() {
        // Prepare data
        User mockUser = new User();
        mockUser.setId(1L);

        BankTransfer debitTransfer = new BankTransfer();
        debitTransfer.setType("debit");
        debitTransfer.setDescription("description");
        BankTransfer creditTransfer = new BankTransfer();
        creditTransfer.setType("credit");
        creditTransfer.setDescription("description");

        List<BankTransfer> bankTransferList = Arrays.asList(debitTransfer, creditTransfer);
        Page<BankTransfer> bankTransferPage = new PageImpl<>(bankTransferList);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
        when(bankTransferRepository.findBankTransferByBankAccount_User(any(User.class), any(Pageable.class))).thenReturn(bankTransferPage);

        Page<TransactionDTO> transactionDTOPage = bankTransferService.getTransferDetails(PageRequest.of(0, 5));

        assertEquals(2, transactionDTOPage.getTotalElements());
        assertEquals(bankTransferList.get(0).getDescription(), transactionDTOPage.getContent().get(0).getDescription());
        assertEquals(bankTransferList.get(1).getDescription(), transactionDTOPage.getContent().get(1).getDescription());

    }
}
