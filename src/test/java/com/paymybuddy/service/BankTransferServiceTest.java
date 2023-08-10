package com.paymybuddy.service;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.BankAccountTransferMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private BankAccountTransferMapper mapper;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankTransferService = new BankTransferServiceImpl(userRepository, bankAccountRepository, bankTransferRepository, bankAccountService, mapper);
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
    public void testProcessCreditTransaction() {

        BankTransferCreateDTO bankTransferDTO = new BankTransferCreateDTO();
        bankTransferDTO.setIban("testIBAN");
        bankTransferDTO.setType(TransactionTypeEnum.TransactionType.CREDIT);
        bankTransferDTO.setAmount(50.0);

        BankAccount mockBankAccount = new BankAccount();
        User mockUser = new User();
        mockUser.setBalance(100.0);
        mockBankAccount.setUser(mockUser);

        // Simuler le comportement du repository
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(mockBankAccount);

        // Exécuter la méthode
        bankTransferService.processBankTransfer(bankTransferDTO);

        // Vérifier les résultats
        assertEquals(150.0, mockUser.getBalance(), 0.01);
    }


    @Test
    void testDebitFromBankAccount_insufficientBalance() {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO("Iban", "descriptionTest", 200.0);


        User user = new User();
        user.setId(1L);
        user.setBalance(100.00);

        BankAccount bankAccount = new BankAccount("iban", "swift", "name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(bankTransferCreateDTO.getIban(), user.getId().intValue())).thenReturn(bankAccount);

        Exception exception = assertThrows(InsufficientBalanceException.class, () -> bankTransferService.processBankTransfer(bankTransferCreateDTO));
        assertEquals("Solde insuffisant pour le transfer.", exception.getMessage());
    }

    @Test
    void testDebitFromBankAccount_negativeAmount() {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setAmount(-100.00);
        bankTransferCreateDTO.setIban("iban");
        bankTransferCreateDTO.setDescription("description");

        User user = new User();
        user.setId(1L);
        user.setBalance(1000.00);

        BankAccount bankAccount = new BankAccount("iban", "swift", "name", user);

        when(bankAccountRepository.findByIbanAndUser_Id(bankTransferCreateDTO.getIban(), user.getId().intValue())).thenReturn(bankAccount);

        Exception exception = assertThrows(NullTransferException.class, () -> bankTransferService.processBankTransfer(bankTransferCreateDTO));
        assertEquals("Le montant de la transaction ne doit pas être nul", exception.getMessage());
    }

    @Test
    void testGetTransferDetails() {
        User mockUser = new User();
        mockUser.setId(1L);

        BankTransfer debitTransfer = new BankTransfer();
        debitTransfer.setType(TransactionTypeEnum.TransactionType.DEBIT);
        debitTransfer.setDescription("description");
        BankTransferInformationDTO debitDTO = new BankTransferInformationDTO();
        debitDTO.setDescription("description");
        debitDTO.setAmount(-100.0); // Assuming this as a sample amount.

        BankTransfer creditTransfer = new BankTransfer();
        creditTransfer.setType(TransactionTypeEnum.TransactionType.CREDIT);
        creditTransfer.setDescription("description");
        BankTransferInformationDTO creditDTO = new BankTransferInformationDTO();
        creditDTO.setDescription("description");
        creditDTO.setAmount(100.0); // Assuming this as a sample amount.

        List<BankTransfer> bankTransferList = Arrays.asList(debitTransfer, creditTransfer);
        Page<BankTransfer> bankTransferPage = new PageImpl<>(bankTransferList);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
        when(bankTransferRepository.findBankTransferByBankAccount_User(any(User.class), any(Pageable.class))).thenReturn(bankTransferPage);
        when(mapper.mapBankTransfer(debitTransfer)).thenReturn(debitDTO);
        when(mapper.mapBankTransfer(creditTransfer)).thenReturn(creditDTO);

        Page<BankTransferInformationDTO> transactionDTOPage = bankTransferService.getTransferDetails(PageRequest.of(0, 5));

        assertEquals(2, transactionDTOPage.getTotalElements());
        assertEquals(debitDTO.getDescription(), transactionDTOPage.getContent().get(0).getDescription());
        assertEquals(creditDTO.getDescription(), transactionDTOPage.getContent().get(1).getDescription());
        assertEquals(creditDTO.getAmount(), transactionDTOPage.getContent().get(1).getAmount());
    }

}
