package com.paymybuddy.service;

import com.paymybuddy.constant.Fee;
import com.paymybuddy.dto.FriendTransactionCreateDTO;
import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.FriendTransactionMapper;
import com.paymybuddy.model.FriendTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.repository.FriendTransactionRepository;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.FriendTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FriendTransactionServiceTest {

    @Mock
    private FriendTransactionService friendTransactionService;

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private FriendTransactionRepository friendTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendTransactionMapper mapper;

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        friendTransactionService = new FriendTransactionServiceImpl(mapper, userRepository, friendTransactionRepository, userConnectionRepository, accountService, balanceService);
    }


    @Test
    public void testSendMoneyWithNullAmount() {
        User receiver = new User();
        receiver.setId(2L);

        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setAmount(null);
        dto.setReceiverUserId(receiver.getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        User user = new User();
        user.setId(userDTO.getId());

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById((1))).thenReturn(Optional.of(user));
        when(userRepository.findById((2))).thenReturn(Optional.of(receiver));


        assertThrows(InvalidAmountException.class, () -> {
            friendTransactionService.sendMoneyToFriend(dto);
        });
    }

    @Test
    public void testSendMoneyWithInsufficientBalance() {
        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(BigDecimal.valueOf(100));

        BigDecimal senderInitialBalance = BigDecimal.valueOf(50);
        BigDecimal transferAmount = BigDecimal.valueOf(60);

        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setAmount(transferAmount);
        dto.setReceiverUserId(receiver.getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        User sender = new User();
        sender.setId(userDTO.getId());
        sender.setBalance(senderInitialBalance);

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById((1))).thenReturn(Optional.of(sender));
        when(userRepository.findById((2))).thenReturn(Optional.of(receiver));
        when(balanceService.calculateFinalPrice(transferAmount)).thenReturn(transferAmount.add(BigDecimal.valueOf(5))); // Supposons que les frais sont de 5

        assertThrows(InsufficientBalanceException.class, () -> {
            friendTransactionService.sendMoneyToFriend(dto);
        });
    }

    @Test
    public void testSendMoneyWithNegativeOrZeroAmount() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setAmount(BigDecimal.valueOf(-10));
        dto.setReceiverUserId(receiver.getId());

        User user = new User();
        user.setId(userDTO.getId());
        user.setBalance(BigDecimal.valueOf(100));

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById((1))).thenReturn(Optional.of(user));
        when(userRepository.findById((2))).thenReturn(Optional.of(receiver));
        when(balanceService.calculateFinalPrice(any())).thenReturn(BigDecimal.TEN);


        assertThrows(NullTransferException.class, () -> {
            friendTransactionService.sendMoneyToFriend(dto);
        });
    }

    @Test
    public void testSendMoneySuccessfully() {
        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(BigDecimal.valueOf(100));

        BigDecimal senderInitialBalance = BigDecimal.valueOf(150);
        BigDecimal transferAmount = BigDecimal.valueOf(60);

        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setAmount(transferAmount);
        dto.setReceiverUserId(receiver.getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        User sender = new User();
        sender.setId(userDTO.getId());
        sender.setBalance(senderInitialBalance);

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(balanceService.calculateFinalPrice(transferAmount)).thenReturn(transferAmount.add(BigDecimal.valueOf(5)));
        when(balanceService.getCurrentUserBalance()).thenReturn(senderInitialBalance);
        when(userConnectionRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(new UserConnection()));
        when(friendTransactionRepository.save(any())).then(returnsFirstArg());

        friendTransactionService.sendMoneyToFriend(dto);

        assertEquals(senderInitialBalance.subtract(transferAmount.add(BigDecimal.valueOf(5))), sender.getBalance());
        assertEquals(BigDecimal.valueOf(100).add(transferAmount), receiver.getBalance());

        verify(friendTransactionRepository).save(any(FriendTransaction.class));
    }

    @Test
    public void testGetTransactionsForUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        User user = new User();
        user.setId(userDTO.getId());

        FriendTransaction friendTransaction = new FriendTransaction();
        friendTransaction.setReceiver(user);
        friendTransaction.setSender(user);

        PageImpl<FriendTransaction> page = new PageImpl<>(Collections.singletonList(friendTransaction));

        FriendTransactionDisplayDTO dto = new FriendTransactionDisplayDTO();
        dto.setAmount(new BigDecimal("100"));
        dto.setCreatedAt(Instant.now());

        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(friendTransactionRepository.findBySenderOrderByCreatedAtDesc(any(), any())).thenReturn(page);
        when(friendTransactionRepository.findByReceiverOrderByCreatedAtDesc(any(), any())).thenReturn(page);
        when(mapper.toFriendTransactionDisplayDTO(any())).thenReturn(dto);

        Page<FriendTransactionDisplayDTO> result = friendTransactionService.getTransactionsForUser(PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getContent().size());
        assertEquals(new BigDecimal("-100"), result.getContent().get(0).getAmount());
    }

}
