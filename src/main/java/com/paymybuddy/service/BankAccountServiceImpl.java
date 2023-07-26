package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
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
    public BankAccount addBankAccount(BankAccountDTO bankAccountDTO) {

        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
            .orElseThrow(() -> new DatabaseException("User does not exist in the database"));


        BankAccount bankAccount = new BankAccount(bankAccountDTO.getIban(), bankAccountDTO.getSwift(), bankAccountDTO.getName(), user);
        bankAccount.setCreatedAt(new Date());

        Iterable<BankAccount> existingAccount = bankAccountRepository.findBankAccountsByUserId(SecurityUtils.getCurrentUserId());

        for (BankAccount account : existingAccount) {
            if(account.getIban().equals(bankAccountDTO.getIban())) {
                throw new DatabaseException(("Iban already exist for this user"));
            }
        }

        try {
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
            throw new DatabaseException("Could not save bank account to database");
        }
    }


    @Override
    public Iterable<BankAccount> getBankAccountByCurrentUserId() {
        Iterable<BankAccount> bankList = bankAccountRepository.findBankAccountsByUserId(SecurityUtils.getCurrentUserId());
        return bankList;
    }

    @Override
    public Page<BankAccount> getSortedBankAccountByCurrentUserId(Pageable pageable) {
        Pageable sortedByCreatedAtDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
                );

        return bankAccountRepository.findBankAccountsByUserId(SecurityUtils.getCurrentUserId(), sortedByCreatedAtDesc);
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
        if(bankAccount.getUser().getBalance() - bankTransferDTO.getAmount() < 0){
            throw new RuntimeException("ERROR");
        }
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() - bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

}
