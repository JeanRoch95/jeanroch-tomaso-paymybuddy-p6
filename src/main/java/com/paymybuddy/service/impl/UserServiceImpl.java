package com.paymybuddy.service.impl;

import com.paymybuddy.dto.UserCreateDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.exceptions.UserAlreadyExistException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        User existingUser = userRepository.findByEmail(userCreateDTO.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistException("Un utilisateur existe déjà avec cette adresse mail");
        }

        User user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setEmail(userCreateDTO.getEmail());
        user.setCreatedAt(Instant.now());

        UserDTO userDTO = mapper.toDTO(user);
        userRepository.save(user);
        return userDTO;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapper.toDTO(userRepository.findByEmail(email));
    }


    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}
