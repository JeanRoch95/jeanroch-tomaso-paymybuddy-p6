package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.Bank;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
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

        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Bank bank = new Bank(iban, swift, name, user.get());

        return bankRepository.save(bank);
    }

    @Override
    public Iterable<Bank> findAllBank(int id) {
        Iterable<Bank> bankList = bankRepository.findAll();
        return bankList;
    }

    @Override
    public void addMoneyToAccount(BankTransferDTO bankTransferDTO, Integer id) {

        Bank bank = bankRepository.findByIban(bankTransferDTO.getIban());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankTransfer.setBank(bank);


        bank.getUser().setBalance(bank.getUser().getBalance() + bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

    @Override
    public void sendMoneyToBank(BankTransferDTO bankTransferDTO, Integer id) {

        Bank bank = bankRepository.findByIban(bankTransferDTO.getIban());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBank(bank);
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bank.getUser().setBalance(bank.getUser().getBalance() - bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

}
