package com.paymybuddy.service;

import com.paymybuddy.model.Bank;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankRepository;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    private final UserRepository userRepository;

    public BankServiceImpl(BankRepository bankRepository, UserRepository userRepository){
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Bank addBank(String iban, String swift, String name) {


        Bank bank = new Bank();
        bank.setIban(iban);
        bank.setSwift(swift);
        bank.setName(name);

        return bankRepository.save(bank);
    }
}
