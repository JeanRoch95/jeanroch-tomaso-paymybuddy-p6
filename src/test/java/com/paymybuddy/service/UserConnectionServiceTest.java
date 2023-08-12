package com.paymybuddy.service;

import com.paymybuddy.dto.UserConnectionDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.ContactNofFoundException;
import com.paymybuddy.exceptions.UserAlreadyAddException;
import com.paymybuddy.mapper.UserConnectionMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserConnectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class UserConnectionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserConnectionService userConnectionService;

    @Mock
    private UserConnectionMapper mapper;

    @Mock
    private UserService userService;


    @BeforeEach
    public void setUpBeforeEachTest() {
        userConnectionService = new UserConnectionServiceImpl(mapper, userRepository, userConnectionRepository, userService);
    }

    @Test
    public void testAddUserConnectionUserNotFound() {
        UserConnectionInformationDTO info = new UserConnectionInformationDTO();
        info.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(ContactNofFoundException.class, () -> userConnectionService.addUserConnection(info));
    }

    @Test
    public void testAddUserConnectionSameUser() {
        UserConnectionInformationDTO info = new UserConnectionInformationDTO();
        info.setEmail("test@example.com");

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        assertThrows(ContactNofFoundException.class, () -> userConnectionService.addUserConnection(info));
    }

    @Test
    public void testAddUserConnectionAlreadyFriends() {
        UserConnectionInformationDTO info = new UserConnectionInformationDTO();
        info.setEmail("test@example.com");

        User receiver = new User();
        receiver.setId(2L);

        User sender = new User();
        sender.setId(1L);

        when(userRepository.findByEmail(anyString())).thenReturn(receiver);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(sender));
        when(userConnectionRepository.findBySenderAndReceiver(any(), any())).thenReturn(Optional.of(new UserConnection()));

        assertThrows(UserAlreadyAddException.class, () -> userConnectionService.addUserConnection(info));
    }

    @Test
    public void testAddUserConnectionSuccess() {
        String email = "test@example.com";
        UserConnectionInformationDTO userConnectionInformationDTO = new UserConnectionInformationDTO();
        userConnectionInformationDTO.setEmail(email);

        User receiver = new User();
        receiver.setId(2L);

        User sender = new User();
        sender.setId(1L);

        UserConnection userConnection = new UserConnection();
        userConnection.setSender(sender);
        userConnection.setReceiver(receiver);
        userConnection.setCreatedAt(Instant.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(receiver);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(sender));
        when(userConnectionRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());
        when(userConnectionRepository.save(any())).thenReturn(userConnection);


        UserConnectionDTO result = userConnectionService.addUserConnection(userConnectionInformationDTO);

        assertNotNull(result);

        verify(userRepository).findByEmail(email);
        verify(userConnectionRepository).save(any(UserConnection.class));
    }

    @Test
    public void testGetFriendConnectionList() {
        User currentUser = new User();
        currentUser.setId(1L);

        UserConnection connection1 = new UserConnection();
        UserConnection connection2 = new UserConnection();

        List<UserConnection> connections = Arrays.asList(connection1, connection2);
        Page<UserConnection> connectionPage = new PageImpl<>(connections);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(currentUser));
        when(userConnectionRepository.findBySenderOrderByCreatedAtDesc(any(User.class), any(Pageable.class))).thenReturn(connectionPage);
        when(mapper.getFriendConnectionList(any(UserConnection.class))).thenReturn(new UserConnectionInformationDTO()); // Configurez ceci selon votre implémentation

        Pageable pageable = PageRequest.of(0, 5);
        Page<UserConnectionInformationDTO> result = userConnectionService.getFriendConnectionList(pageable);

        assertNotNull(result);
        assertEquals(connections.size(), result.getContent().size());

        verify(userRepository).findById(1);
        verify(userConnectionRepository).findBySenderOrderByCreatedAtDesc(currentUser, pageable);
    }
}
