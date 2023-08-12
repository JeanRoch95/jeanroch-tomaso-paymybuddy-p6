package com.paymybuddy.service;

import com.paymybuddy.dto.UserCreateDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

public interface UserService {

    UserDTO createUser(UserCreateDTO userCreateDTO);

    UserDTO getUserByEmail(String email);

    UserDTO getUserByCurrentId();

    UserInformationDTO getCurrentUserInformation(UserDTO userDTO);

    void updateCurrentUserInformation(UserInformationDTO userInformationDTO);

    UserDTO getCurrentUser();

    Boolean checkIfEmailChanged(UserInformationDTO userDto);

    void logoutUser(HttpServletRequest request, HttpServletResponse response);
}
