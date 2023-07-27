package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.CheckSufficientBalance;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class BankTransferServiceImpl implements BankTransferService{

    private UserRepository userRepository;

    private BankAccountRepository bankAccountRepository;

    private BankTransferRepository bankTransferRepository;


    public BankTransferServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository, BankTransferRepository bankTransferRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransferRepository = bankTransferRepository;
    }

    @Override
    public Double getUserBalance() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Double balance = user.get().getBalance();
        return balance;
    }

    @Override
    public void addMoneyToAccount(BankTransferDTO bankTransferDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIban(bankTransferDTO.getIban());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankTransfer.setBankAccount(bankAccount);


        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() + bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

    @Override
    public void sendMoneyToBank(BankTransferDTO bankTransferDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIban(bankTransferDTO.getIban());

        if (bankTransferDTO.getAmount() <= 0) {
            throw new NullTransferException("Le montant de la transaction ne doit pas être nul");
        }

        CheckSufficientBalance.checkSufficientBalance(bankAccount, bankTransferDTO.getAmount());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() - bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

}
