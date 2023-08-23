package com.paymybuddy.service;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.exceptions.BankAccountNotFoundException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.BankAccountTransferMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.BankTransferServiceImpl;
import com.paymybuddy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankTransferServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankTransferRepository bankTransferRepository;

    @Mock
    private BankTransferService bankTransferService;

    @Mock
    private BankAccountTransferMapper mapper;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        bankTransferService = new BankTransferServiceImpl(mapper,userRepository, bankAccountRepository, bankTransferRepository, accountService);
    }

    @Test
    void processBankTransfer_WhenValidCredit_ShouldProcessSuccessfully() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        BankTransferCreateDTO dto = new BankTransferCreateDTO();
        dto.setIban("IBAN12345");
        dto.setType(TransactionTypeEnum.TransactionType.CREDIT);
        dto.setAmount(BigDecimal.valueOf(100));

        User mockUser = new User();
        mockUser.setBalance(BigDecimal.valueOf(200));

        BankAccount mockBankAccount = new BankAccount();
        mockBankAccount.setUser(mockUser);

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(mockBankAccount);

        bankTransferService.processBankTransfer(dto);

        assertEquals(BigDecimal.valueOf(300), mockUser.getBalance());
        verify(bankTransferRepository).save(any(BankTransfer.class));
    }

    @Test
    void processBankTransfer_WhenValidDebit_ShouldProcessSuccessfully() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        BankTransferCreateDTO dto = new BankTransferCreateDTO();
        dto.setIban("IBAN12345");
        dto.setType(TransactionTypeEnum.TransactionType.DEBIT);
        dto.setAmount(BigDecimal.valueOf(100));

        User mockUser = new User();
        mockUser.setBalance(BigDecimal.valueOf(200));

        BankAccount mockBankAccount = new BankAccount();
        mockBankAccount.setUser(mockUser);

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(mockBankAccount);

        bankTransferService.processBankTransfer(dto);

        assertEquals(BigDecimal.valueOf(100), mockUser.getBalance());
        verify(bankTransferRepository).save(any(BankTransfer.class));
    }


    @Test
    public void processBankTransferTest_BankAccountNotFound() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        BankTransferCreateDTO dto = new BankTransferCreateDTO();
        dto.setIban("IBAN12345");

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(null);

        Exception e = assertThrows(BankAccountNotFoundException.class, () -> {
            bankTransferService.processBankTransfer(dto);
        });

        assertEquals("Le compte bancaire n'existe pas.", e.getMessage());
    }

    @Test
    void processBankTransfer_WhenBankAccountNotFound_ShouldThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        BankTransferCreateDTO dto = new BankTransferCreateDTO();
        dto.setIban("IBAN12345");

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(null);

        Exception exception = assertThrows(BankAccountNotFoundException.class, () -> bankTransferService.processBankTransfer(dto));

        assertEquals("Le compte bancaire n'existe pas.", exception.getMessage());
    }

    @Test
    void processBankTransfer_WhenInvalidAmountForCredit_ShouldThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        BankTransferCreateDTO dto = new BankTransferCreateDTO();
        dto.setIban("IBAN12345");
        dto.setType(TransactionTypeEnum.TransactionType.CREDIT);
        dto.setAmount(null);

        BankAccount mockBankAccount = new BankAccount();
        when((accountService.getCurrentAccount())).thenReturn(userDTO);
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(mockBankAccount);

        Exception exception = assertThrows(InvalidAmountException.class, () -> bankTransferService.processBankTransfer(dto));
        assertEquals("Le montant n'est pas valide.", exception.getMessage());
    }

    @Test
    void processBankTransfer_WhenZeroOrNegativeAmountForCredit_ShouldThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        BankTransferCreateDTO dto = new BankTransferCreateDTO();
        dto.setIban("IBAN12345");
        dto.setType(TransactionTypeEnum.TransactionType.CREDIT);
        dto.setAmount(BigDecimal.valueOf(-10));

        BankAccount mockBankAccount = new BankAccount();
        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(bankAccountRepository.findByIbanAndUser_Id(anyString(), anyInt())).thenReturn(mockBankAccount);

        Exception exception = assertThrows(NullTransferException.class, () -> bankTransferService.processBankTransfer(dto));
        assertEquals("Le montant de la transaction ne doit pas être nul", exception.getMessage());
    }

// Ajoutez d'autres tests pour DEBIT, solde insuffisant, etc.



    @Test
    public void getTransferDetailsTest_UserNotFound() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bankTransferService.getTransferDetails(pageable);
        });

        assertEquals("Utilisateur inexistant", e.getMessage());
    }

    @Test
    public void getTransferDetailsTest_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        User mockUser = new User();
        userDTO.setId(1L);

        List<BankTransfer> bankTransferList = Arrays.asList(new BankTransfer(), new BankTransfer());

        Page<BankTransfer> mockPageTransfers = new PageImpl<>(bankTransferList);

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
        when(bankTransferRepository.findBankTransferByBankAccount_User(any(User.class), any(Pageable.class))).thenReturn(mockPageTransfers);

        Page<BankTransferInformationDTO> result = bankTransferService.getTransferDetails(pageable);

        assertNotNull(result);
    }



}
