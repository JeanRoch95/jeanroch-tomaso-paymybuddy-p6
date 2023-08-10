package com.paymybuddy.service;

import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapper.toDTO(userRepository.findByEmail(email));
    }

    @Override
    public UserDTO getUserByCurrentId() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());

        if(!user.isPresent()) {
            throw new UserNotFoundException("Error Utilisateur introuvable");
        }

        return mapper.toDTO(user.get());
    }

    @Override
    public UserInformationDTO getCurrentUserInformation(UserDTO userDTO) {
        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName(userDTO.getFirstName());
        userInformationDTO.setLastName(userDTO.getLastName());
        userInformationDTO.setEmail(userDTO.getEmail());

        return userInformationDTO;
    }

    @Override
    public void updateCurrentUserInformation(UserInformationDTO userInformationDTO) {

        UserDTO userDTO = getUserByCurrentId();

        userDTO.setFirstName(userInformationDTO.getFirstName());
        userDTO.setLastName(userInformationDTO.getLastName());
        userDTO.setEmail(userInformationDTO.getEmail());
        userDTO.setUpdatedAt(Instant.now());

        userRepository.save(mapper.fromDTO(userDTO));
    }
}
