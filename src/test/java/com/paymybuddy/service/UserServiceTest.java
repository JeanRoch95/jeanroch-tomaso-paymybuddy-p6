package com.paymybuddy.service;

import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


    @BeforeEach
    public void setUpBeforeEachTest() {
        userService = new UserServiceImpl(userRepository, mapper);
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
    public void testGetUserByCurrentId() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toDTO(user)).thenReturn(new UserDTO());

        UserDTO result = userService.getUserByCurrentId();

        assertNotNull(result);
    }

    @Test
    void testGetUserByCurrentId_userNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUserByCurrentId());
        assertEquals("Error Utilisateur introuvable", exception.getMessage());
    }


    @Test
    public void testGetCurrentUserInformation() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");

        UserInformationDTO result = userService.getCurrentUserInformation(userDTO);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testUpdateCurrentUserInformation() {
        UserDTO existingUserDTO = new UserDTO();
        existingUserDTO.setId(1L);
        existingUserDTO.setFirstName("OldFirstName");
        existingUserDTO.setLastName("OldLastName");
        existingUserDTO.setEmail("old@email.com");

        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName("NewFirstName");
        userInformationDTO.setLastName("NewLastName");
        userInformationDTO.setEmail("new@email.com");

        User userToUpdate = new User();
        userToUpdate.setId(1L);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userToUpdate));
        when(mapper.toDTO(any(User.class))).thenReturn(existingUserDTO);
        when(mapper.fromDTO(any(UserDTO.class))).thenReturn(userToUpdate);

        userService.updateCurrentUserInformation(userInformationDTO);

        assertEquals(userInformationDTO.getFirstName(), existingUserDTO.getFirstName());
        assertEquals(userInformationDTO.getLastName(), existingUserDTO.getLastName());
        assertEquals(userInformationDTO.getEmail(), existingUserDTO.getEmail());

        verify(userRepository, times(1)).save(userToUpdate);
    }



}
