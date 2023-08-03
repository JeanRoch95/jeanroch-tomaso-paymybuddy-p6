package com.paymybuddy.service;

import com.paymybuddy.model.User;
import org.springframework.stereotype.Service;

public interface UserService {

    User getUserByEmail(String email);
}
