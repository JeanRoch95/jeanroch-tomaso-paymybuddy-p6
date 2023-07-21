package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.exceptions.DatabaseException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final UserRepository userRepository;

    private final BankTransferRepository bankTransferRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository, BankTransferRepository bankTransferRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankTransferRepository = bankTransferRepository;
    }

    @Override
    public BankAccount addBank(String iban, String swift, String name) {

        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        BankAccount bankAccount = new BankAccount(iban, swift, name, user.get());

        try {
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
            throw new DatabaseException("Could not save bank account to database");
        }

    }


    @Override
    public Iterable<BankAccount> getBankAccountByUserId(int id) {
        Iterable<BankAccount> bankList = bankAccountRepository.findBankAccountsByUserId(id);
        return bankList;
    }

    @Override
    public Page<BankAccount> getBankAccountByUserId(int id, Pageable pageable) {
        return bankAccountRepository.findBankAccountsByUserId(id, pageable);
    }


    @Override
    public void addMoneyToAccount(BankTransferDTO bankTransferDTO, Integer id) {

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
    public void sendMoneyToBank(BankTransferDTO bankTransferDTO, Integer id) {

        BankAccount bankAccount = bankAccountRepository.findByIban(bankTransferDTO.getIban());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() - bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

}
