package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferDisplayDTO;
import com.paymybuddy.exceptions.DatabaseException;
import com.paymybuddy.exceptions.IbanAlreadyExistsException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.BankAccountMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final UserRepository userRepository;

    private BankAccountMapper bankAccountMapper;


    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository, BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public BankAccountDTO addBankAccount(BankTransferDisplayDTO bankTransferDisplayDTO) {

        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
            .orElseThrow(() -> new UserNotFoundException("Erreur 404 - BAD REQUEST")); //


        BankAccountDTO bankAccountDTO = new BankAccountDTO(bankTransferDisplayDTO.getIban(), bankTransferDisplayDTO.getSwift(), bankTransferDisplayDTO.getName());
        bankAccountDTO.setCreatedAt(Instant.now()); // TODO Heure GMT

        Iterable<BankAccount> existingAccount = bankAccountRepository.findByUserId(SecurityUtils.getCurrentUserId());

        for (BankAccount account : existingAccount) {
            if(account.getIban().equals(bankAccountDTO.getIban())) {
                throw new IbanAlreadyExistsException(("Vous avez déjà ajouté cet iban a votre liste de compte"));
            }
        }

        BankAccount bankAccount = new BankAccount(bankAccountDTO.getIban(), bankAccountDTO.getSwift(), bankAccountDTO.getName(), user);
        bankAccount.setCreatedAt(bankAccountDTO.getCreatedAt());

        try {
            BankAccount savedAccount = bankAccountRepository.save(bankAccount);
            return bankAccountMapper.toDTO(savedAccount);
        } catch (Exception e) {
            throw new DatabaseException("Erreur 404 - BAD REQUEST");
        }
    }

    @Override
    public Iterable<BankAccountDTO> getBankAccountByCurrentUserId() {
        Iterable<BankAccount> bankList = bankAccountRepository.findByUserId(SecurityUtils.getCurrentUserId());

        List<BankAccountDTO> bankAccountDtoList = StreamSupport.stream(bankList.spliterator(), false)
                .map(bankAccount -> new BankAccountDTO(bankAccount))
                .collect(Collectors.toList());

        return bankAccountDtoList;
    }

    @Override
    public Page<BankAccountDTO> getSortedBankAccountByCurrentUserId(Pageable pageable) {
        Page<BankAccount> bankAccountPage = bankAccountRepository.findByUserIdOrderByCreatedAtDesc(SecurityUtils.getCurrentUserId(), pageable);

        Page<BankAccountDTO> bankAccountDtoPage = bankAccountPage.map(bankAccount -> new BankAccountDTO(bankAccount));

        return bankAccountDtoPage;
    }

}
