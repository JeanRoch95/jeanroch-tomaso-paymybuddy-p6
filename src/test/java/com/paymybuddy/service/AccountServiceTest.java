package com.paymybuddy.service;

import com.paymybuddy.dto.*;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.exceptions.WrongPasswordException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class AccountServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUpBeforeEachTest() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@email.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        accountService = new AccountServiceImpl(userRepository, userMapper, bankAccountRepository, passwordEncoder);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentAccount() {
        String expectedEmail = "test@email.com";
        User mockUser = new User();
        UserDTO mockUserDTO = new UserDTO();

        when(userRepository.findByEmail(expectedEmail)).thenReturn(mockUser);
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);

        UserDTO result = accountService.getCurrentAccount();

        verify(userRepository).findByEmail(expectedEmail);
        verify(userMapper).toDTO(mockUser);
        assertEquals(mockUserDTO, result);
    }

    @Test
    void testGetBankAccountByCurrentUserId() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);

        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setId(1L);
        bankAccount1.setUser(new User(mockUserDTO.getId()));
        bankAccount1.setIban("testIban1");

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setId(2L);
        bankAccount2.setUser(new User(mockUserDTO.getId()));
        bankAccount2.setIban("testIban2");

        List<BankAccount> bankAccountList = Arrays.asList(bankAccount1, bankAccount2);

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(bankAccountRepository.findByUserId(mockUserDTO.getId().intValue())).thenReturn(bankAccountList);

        Iterable<BankAccountDTO> result = accountService.getBankAccountByCurrentUserId();

        assertNotNull(result);
        List<BankAccountDTO> resultList = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());
        assertEquals(2, resultList.size());
        assertEquals(bankAccount1.getIban(), resultList.get(0).getIban());
        assertEquals(bankAccount2.getIban(), resultList.get(1).getIban());

        verify(bankAccountRepository).findByUserId(anyInt());
    }

    @Test
    public void testGetCurrentAccountInformations() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setFirstName("John");
        mockUserDTO.setLastName("Doe");
        mockUserDTO.setEmail("john.doe@example.com");

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);

        UserInformationDTO result = accountService.getCurrentAccountInformations();

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    public void testEmailHasNotChanged() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setEmail("original.email@example.com");

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);

        UserInformationDTO inputDto = new UserInformationDTO();
        inputDto.setEmail("original.email@example.com");

        Boolean result = accountService.checkIfEmailChanged(inputDto);

        assertFalse(result);
    }

    @Test
    public void testEmailHasChanged() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setEmail("original.email@example.com");

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);

        UserInformationDTO inputDto = new UserInformationDTO();
        inputDto.setEmail("new.email@example.com");

        Boolean result = accountService.checkIfEmailChanged(inputDto);

        assertTrue(result);
    }


    @Test
    void testUpdateCurrentUserInformation() {
        UserDTO existingUserDTO = new UserDTO();
        existingUserDTO.setId(1L);
        existingUserDTO.setFirstName("OldFirstName");
        existingUserDTO.setLastName("OldLastName");
        existingUserDTO.setEmail("old@email.com");

        when(accountService.getCurrentAccount()).thenReturn(existingUserDTO);

        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName("NewFirstName");
        userInformationDTO.setLastName("NewLastName");
        userInformationDTO.setEmail("new@email.com");

        User updatedUser = new User();
        updatedUser.setFirstName("NewFirstName");
        updatedUser.setLastName("NewLastName");
        updatedUser.setEmail("new@email.com");


        when(userMapper.fromDTO(any(UserDTO.class))).thenReturn(updatedUser);

        accountService.updateCurrentAccountInformation(userInformationDTO);

        assertEquals(userInformationDTO.getFirstName(), existingUserDTO.getFirstName());
        assertEquals(userInformationDTO.getLastName(), existingUserDTO.getLastName());
        assertEquals(userInformationDTO.getEmail(), existingUserDTO.getEmail());

        verify(userMapper).fromDTO(any(UserDTO.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdatePasswordSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setPassword("oldEncodedPassword");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("oldEncodedPassword");

        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setCurrentPassword("oldPassword");
        passwordDTO.setNewPassword("newPassword");

        when(userMapper.fromDTO(userDTO)).thenReturn(user);
        when(accountService.getCurrentAccount()).thenReturn(userDTO);
        when(passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(passwordDTO.getNewPassword())).thenReturn("newEncodedPassword");

        accountService.updateCurrentAccountPassword(passwordDTO);

        verify(passwordEncoder).encode(passwordDTO.getNewPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updateCurrentAccountPassword_UserNotFound() {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setCurrentPassword("currentPassword");
        passwordDTO.setNewPassword("newPassword");

        when(accountService.getCurrentAccount()).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> accountService.updateCurrentAccountPassword(passwordDTO));

        assertEquals("L'utilisateur actuel n'existe pas.", exception.getMessage());
    }

    @Test
    void updateCurrentAccountPassword_WrongCurrentPassword() {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setCurrentPassword("wrongPassword");
        passwordDTO.setNewPassword("newPassword");

        UserDTO mockUserDTO = new UserDTO();
        User mockUser = new User();
        mockUser.setPassword("encodedCurrentPassword");

        when(accountService.getCurrentAccount()).thenReturn(mockUserDTO);
        when(userMapper.fromDTO(mockUserDTO)).thenReturn(mockUser);
        when(passwordEncoder.matches(passwordDTO.getCurrentPassword(), mockUser.getPassword())).thenReturn(false);

        Exception exception = assertThrows(WrongPasswordException.class,
                () -> accountService.updateCurrentAccountPassword(passwordDTO));

        assertEquals("Le mot de passe entré ne correspond pas avec votre mot de passe actuel", exception.getMessage());
    }
}
