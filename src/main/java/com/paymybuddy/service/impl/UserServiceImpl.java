package com.paymybuddy.service.impl;

import com.paymybuddy.dto.UserCreateDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.exceptions.UserAlreadyExistException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper mapper;

    private PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        User existingUser = userRepository.findByEmail(userCreateDTO.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistException("L'utilisateur existe déjà");
        }

        User user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setEmail(userCreateDTO.getEmail());
        user.setCreatedAt(Instant.now()); // TODO Voir si le createdAt est necessaire dans le DTO

        UserDTO userDTO = mapper.toDTO(user);
        userRepository.save(user);
        return userDTO;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapper.toDTO(userRepository.findByEmail(email));
    }

    @Override
    public UserDTO getUserByCurrentId() { // TODO Peut etre utiliser que le getCurrentUser
        Optional<User> user = userRepository.findById(getCurrentUser().getId().intValue());

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

    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return mapper.toDTO(user);
    }

    @Override
    public Boolean checkIfEmailChanged(UserInformationDTO userDto) {
        String currentEmail = getCurrentUser().getEmail();
        return !currentEmail.equals(userDto.getEmail());
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}
