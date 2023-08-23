package com.paymybuddy.service;

import com.paymybuddy.dto.UserCreateDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.exceptions.UserAlreadyExistException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Mock
    private Authentication authentication;


    @BeforeEach
    public void setUpBeforeEachTest() {
        userService = new UserServiceImpl(userRepository, mapper, passwordEncoder, securityContextLogoutHandler);
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testCreateUserWhenUserAlreadyExists() {
        // Given
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("existing@test.com");

        User existingUser = new User();
        existingUser.setEmail("existing@test.com");

        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(existingUser);

        assertThrows(UserAlreadyExistException.class, () -> {
            userService.createUser(userCreateDTO);
        });
    }

    @Test
    public void testCreateUserSuccessfully() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("John");
        userCreateDTO.setLastName("Doe");
        userCreateDTO.setEmail("john.doe@test.com");
        userCreateDTO.setPassword("test1234");

        User createdUser = new User();
        createdUser.setFirstName(userCreateDTO.getFirstName());
        createdUser.setLastName(userCreateDTO.getLastName());
        createdUser.setEmail(userCreateDTO.getEmail());
        createdUser.setPassword(userCreateDTO.getPassword());

        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setFirstName(userCreateDTO.getFirstName());
        expectedUserDTO.setLastName(userCreateDTO.getLastName());
        expectedUserDTO.setEmail(userCreateDTO.getEmail());

        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(userCreateDTO.getPassword())).thenReturn(userCreateDTO.getPassword());
        doReturn(expectedUserDTO).when(mapper).toDTO(any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        UserDTO result = userService.createUser(userCreateDTO);

        assertEquals(expectedUserDTO, result);
        verify(userRepository).save(any(User.class));
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

    @Test
    public void testLogoutUser_whenAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doNothing().when(securityContextLogoutHandler).logout(request, response, authentication);

        userService.logoutUser(request, response);

        verify(securityContextLogoutHandler, times(1)).logout(request, response, authentication);
    }

    @Test
    public void testLogoutUser_whenNotAuthenticated() {
        userService.logoutUser(request, response);

        verify(securityContextLogoutHandler, times(0)).logout(any(), any(), any());
    }

}
