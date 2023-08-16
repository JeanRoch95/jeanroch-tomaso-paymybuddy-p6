package com.paymybuddy.service.impl;

import com.paymybuddy.dto.UserConnectionDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.ContactNofFoundException;
import com.paymybuddy.exceptions.UserAlreadyAddException;
import com.paymybuddy.mapper.UserConnectionMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.UserConnectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserConnectionServiceImpl implements UserConnectionService {

    private final UserConnectionMapper mapper;

    private final UserRepository userRepository;

    private final UserConnectionRepository userConnectionRepository;

    private final AccountService accountService;

    public UserConnectionServiceImpl(UserConnectionMapper mapper, UserRepository userRepository, UserConnectionRepository userConnectionRepository, AccountService accountService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.userConnectionRepository = userConnectionRepository;
        this.accountService = accountService;
    }

    @Override
    public UserConnectionDTO addUserConnection(UserConnectionInformationDTO userConnectionInformationDTO) {
        User receiver = userRepository.findByEmail(userConnectionInformationDTO.getEmail());

        if (receiver == null || receiver.getId() == accountService.getCurrentAccount().getId().intValue()) {
            throw new ContactNofFoundException("Utilisateur introuvable");
        }

        Optional<User> sender = userRepository.findById(accountService.getCurrentAccount().getId().intValue());

        Optional<UserConnection> existingConnection = userConnectionRepository.findBySenderAndReceiver(sender.get(), receiver);
        if (existingConnection.isPresent()) {
            throw new UserAlreadyAddException("Vous êtes déjà amis avec cet utilisateur");
        }

        UserConnection userConnection = new UserConnection();
        userConnection.setSender(sender.get());
        userConnection.setReceiver(receiver);
        userConnection.setCreatedAt(Instant.now().plus(2, ChronoUnit.HOURS));

        UserConnection savedConnection = userConnectionRepository.save(userConnection);

        return new UserConnectionDTO(savedConnection);
    }

    @Override
    public Page<UserConnectionInformationDTO> getFriendConnectionList(Pageable pageable) {
        Optional<User> user = userRepository.findById(accountService.getCurrentAccount().getId().intValue());
        Page<UserConnection> userConnections = userConnectionRepository.findBySenderOrderByCreatedAtDesc(user.get(), pageable);

        List<UserConnectionInformationDTO> dtos = userConnections.getContent().stream()
                .map(mapper::getFriendConnectionList)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, userConnections.getTotalElements());
    }

}
