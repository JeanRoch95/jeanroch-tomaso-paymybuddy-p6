package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountCreateDTO;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final UserRepository userRepository;

    private BankAccountMapper bankAccountMapper;

    private UserService userService;


    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository, BankAccountMapper bankAccountMapper, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.userService = userService;
    }

    @Override
    public BankAccountDTO addBankAccount(BankAccountCreateDTO bankAccountCreateDTO) {

        User user = userRepository.findById(userService.getCurrentUser().getId().intValue())
            .orElseThrow(() -> new UserNotFoundException("Erreur 404 - BAD REQUEST")); //


        BankAccountDTO bankAccountDTO = new BankAccountDTO(bankAccountCreateDTO.getIban(), bankAccountCreateDTO.getSwift(), bankAccountCreateDTO.getName());
        bankAccountDTO.setCreatedAt(Instant.now());

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
        Iterable<BankAccount> bankList = bankAccountRepository.findByUserId(userService.getCurrentUser().getId().intValue());

        List<BankAccountDTO> bankAccountDtoList = StreamSupport.stream(bankList.spliterator(), false)
                .map(bankAccount -> new BankAccountDTO(bankAccount))
                .collect(Collectors.toList());

        return bankAccountDtoList;
    }

    @Override
    public Page<BankAccountDTO> getSortedBankAccountByCurrentUserId(Pageable pageable) {
        Page<BankAccount> bankAccountPage = bankAccountRepository.findByUserIdOrderByCreatedAtDesc(userService.getCurrentUser().getId().intValue(), pageable);

        Page<BankAccountDTO> bankAccountDtoPage = bankAccountPage.map(bankAccount -> new BankAccountDTO(bankAccount));

        return bankAccountDtoPage;
    }

}
