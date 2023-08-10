package com.paymybuddy.service;

import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.model.User;
import org.springframework.stereotype.Service;

public interface UserService {

    User getUserByEmail(String email);

    UserDTO getUserByCurrentId();

    UserInformationDTO getCurrentUserInformation(UserDTO userDTO);

    void updateCurrentUserInformation(UserInformationDTO userInformationDTO);
}
