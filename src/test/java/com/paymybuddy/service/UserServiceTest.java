package com.paymybuddy.service;

import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setUpBeforeEachTest() {
        userService = new UserServiceImpl(userRepository, mapper, passwordEncoder);
    }

    @Test
    public void testGetUserByEmail() {
        // Given
        String email = "test@test.com";
        User user = new User();
        user.setEmail(email);
        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(mapper.toDTO(user)).thenReturn(expectedUserDTO);

        UserDTO result = userService.getUserByEmail(email);

        assertEquals(expectedUserDTO, result);
    }

}
