package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.Bank;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    private final UserRepository userRepository;

    private final BankTransferRepository bankTransferRepository;

    public BankServiceImpl(BankRepository bankRepository, UserRepository userRepository, BankTransferRepository bankTransferRepository) {
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
        this.bankTransferRepository = bankTransferRepository;
    }

    @Override
    public Bank addBank(String iban, String swift, String name) {


        Bank bank = new Bank();
        bank.setIban(iban);
        bank.setSwift(swift);
        bank.setName(name);

        return bankRepository.save(bank);
    }

    @Override
    public void addMoneyToAccount(BankTransferDTO bankTransferDTO, Integer id) {

        Optional<User> user = userRepository.findById(id);

        Bank bank = bankRepository.findByIban(bankTransferDTO.getIban());


        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankTransfer.setBank(bank);
        user.get().setBalance(user.get().getBalance() + bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

}
