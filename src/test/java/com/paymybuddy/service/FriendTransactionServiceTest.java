package com.paymybuddy.service;

import com.paymybuddy.mapper.FriendTransactionMapper;
import com.paymybuddy.repository.FriendTransactionRepository;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private FriendTransactionMapper friendTransactionMapper;

    @BeforeEach
    public void setUpBeforeEachTest() {
        friendTransactionService = new FriendTransactionServiceImpl(friendTransactionMapper, userRepository, friendTransactionRepository, userConnectionRepository);
    }



}
