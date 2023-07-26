package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankTransferServiceImpl implements BankTransferService{

    private UserRepository userRepository;


    public BankTransferServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Double getUserBalance(int id) {
        Optional<User> user = userRepository.findById(id);
        Double balance = user.get().getBalance();
        return balance;
    }
}
