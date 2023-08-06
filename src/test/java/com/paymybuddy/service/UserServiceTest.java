package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @Mock
    private UserService userService;

    @Mock private UserRepository userRepository;

    @BeforeEach
    public void setUpBeforeEachTest() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "test@test.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.getUserByEmail(email);

        assertEquals(user, result);
    }

}
