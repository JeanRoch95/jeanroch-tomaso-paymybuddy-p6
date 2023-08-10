package com.paymybuddy.service;

import com.jayway.jsonpath.internal.function.sequence.First;
import com.paymybuddy.constant.Fee;
import com.paymybuddy.dto.FriendTransactionCreateDTO;
import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.FriendTransactionMapper;
import com.paymybuddy.mapper.FriendTransactionMapperTest;
import com.paymybuddy.model.FriendTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.repository.FriendTransactionRepository;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.validation.constraints.Null;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    public void setUpBeforeEachTest() {
        friendTransactionService = new FriendTransactionServiceImpl(mapper, userRepository, friendTransactionRepository, userConnectionRepository);
    }

    @Test
    public void testGetCurrentUserBalance() {
        Double expectedBalance = 100.0;
        User mockUser = new User();
        mockUser.setBalance(expectedBalance);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Double actualBalance = friendTransactionService.getCurrentUserBalance();

        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    public void testSendMoneyToFriend_InsufficientBalance() {
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(100.0);

        User receiver = new User();
        receiver.setId(2L);


        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setAmount(200);
        dto.setReceiverUserId(receiver.getId());


        when(userRepository.findById(1)).thenReturn(Optional.of(sender));

        assertThrows(InsufficientBalanceException.class, () -> {
            friendTransactionService.sendMoneyToFriend(dto);
        });
    }

    @Test
    public void testSendMoneyToFriend_NullTransfer() {
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(100.0);

        User receiver = new User();
        receiver.setId(2L);

        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setAmount(-10);
        dto.setReceiverUserId(receiver.getId());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(sender));

        assertThrows(NullTransferException.class, () -> {
            friendTransactionService.sendMoneyToFriend(dto);
        });
    }

    @Test
    public void testSendMoneyToFriend() {
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(200.0);

        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(100.0);

        UserConnection connection = new UserConnection();
        connection.setSender(sender);
        connection.setReceiver(receiver);

        FriendTransactionCreateDTO dto = new FriendTransactionCreateDTO();
        dto.setReceiverUserId(receiver.getId());
        dto.setAmount(100.0);
        dto.setDescription("Test Transaction");

        Double fee = dto.getAmount() * Fee.FRIEND_TRANSACTION_FEES;
        Double senderFinalBalance = sender.getBalance() - friendTransactionService.calculateFinalPrice(dto.getAmount());
        Double receiverFinalBalance = receiver.getBalance() + dto.getAmount();


        when(userRepository.findById(sender.getId().intValue())).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiver.getId().intValue())).thenReturn(Optional.of(receiver));
        when(userConnectionRepository.findBySenderAndReceiver(any(), any())).thenReturn(Optional.of(connection));



        friendTransactionService.sendMoneyToFriend(dto);

        assertEquals(senderFinalBalance, sender.getBalance(), "Sender's balance should be decremented by amount and fee");
        assertEquals(receiverFinalBalance, receiver.getBalance(), "Receiver's balance should be incremented by the amount only");
        }

    @Test
    public void testCalculateFinalPrice() {
        Double amount = 100.0;
        Double expectedFinalPrice = amount + (amount * Fee.FRIEND_TRANSACTION_FEES);
        Double actualFinalPrice = friendTransactionService.calculateFinalPrice(amount);

        assertEquals(expectedFinalPrice, actualFinalPrice, 0.001, "Expected and actual final prices should match");
    }

    @Test
    public void testCalculateMaxPrice() {
        Double balance = 1000.0;
        Double expectedMaxPrice = balance - (balance * Fee.FRIEND_TRANSACTION_FEES);
        Double actualMaxPrice = friendTransactionService.calculateMaxPrice(balance);

        assertEquals(expectedMaxPrice, actualMaxPrice, 0.001, "Expected and actual max prices should match");
    }

    @Test
    public void testGetTransactionsForUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("UserTest");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setFirstName("ReceiverTest");

        Pageable pageable = PageRequest.of(0, 10);


        when(userRepository.findById(user.getId().intValue())).thenReturn(Optional.of(user));

        FriendTransaction sentTransaction = new FriendTransaction();
        FriendTransaction receivedTransaction = new FriendTransaction();

        sentTransaction.setReceiver(receiver);
        receivedTransaction.setSender(user);

        when(friendTransactionRepository.findBySenderOrderByCreatedAtDesc(user, pageable)).thenReturn(new PageImpl<>(Arrays.asList(sentTransaction)));
        when(friendTransactionRepository.findByReceiverOrderByCreatedAtDesc(user, pageable)).thenReturn(new PageImpl<>(Arrays.asList(receivedTransaction)));

        FriendTransactionDisplayDTO sentDTO = new FriendTransactionDisplayDTO();
        sentDTO.setAmount(100.0);
        FriendTransactionDisplayDTO receivedDTO = new FriendTransactionDisplayDTO();
        receivedDTO.setAmount(100.0);
        when(mapper.toFriendTransactionDisplayDTO(sentTransaction)).thenReturn(sentDTO);
        when(mapper.toFriendTransactionDisplayDTO(receivedTransaction)).thenReturn(receivedDTO);

        Page<FriendTransactionDisplayDTO> result = friendTransactionService.getTransactionsForUser(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(sentDTO));
        assertTrue(result.getContent().contains(receivedDTO));


    }
}
