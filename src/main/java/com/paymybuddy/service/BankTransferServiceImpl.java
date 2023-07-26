package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankTransferServiceImpl implements BankTransferService{

    private UserRepository userRepository;


    public BankTransferServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Double getUserBalance() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Double balance = user.get().getBalance();
        return balance;
    }
}
