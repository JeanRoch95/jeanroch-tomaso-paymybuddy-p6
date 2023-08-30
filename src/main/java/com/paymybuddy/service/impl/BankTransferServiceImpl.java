package com.paymybuddy.service.impl;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.*;
import com.paymybuddy.mapper.BankAccountTransferMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BankTransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
public class BankTransferServiceImpl implements BankTransferService {

    private final BankAccountTransferMapper mapper;

    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    private final BankTransferRepository bankTransferRepository;

    private final AccountService accountService;

    public BankTransferServiceImpl(BankAccountTransferMapper mapper, UserRepository userRepository, BankAccountRepository bankAccountRepository, BankTransferRepository bankTransferRepository, AccountService accountService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransferRepository = bankTransferRepository;
        this.accountService = accountService;
    }

    @Override
    public void processBankTransfer(BankTransferCreateDTO bankTransferCreateDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIbanAndUser_Id(bankTransferCreateDTO.getIban(), accountService.getCurrentAccount().getId().intValue());

        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Le compte bancaire n'existe pas.");
        }

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferCreateDTO.getAmount());
        bankTransfer.setDescription(bankTransferCreateDTO.getDescription());
        bankTransfer.setCreatedAt(Instant.now());


        if (TransactionTypeEnum.TransactionType.CREDIT.equals(bankTransferCreateDTO.getType())) {
            if (bankTransferCreateDTO.getAmount() == null) {
                throw new InvalidAmountException("Le montant n'est pas valide.");
            }
            if (bankTransferCreateDTO.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
                throw new NullTransferException("Le montant de la transaction ne doit pas être nul");
            }
            bankTransfer.setType(TransactionTypeEnum.TransactionType.CREDIT);
            BigDecimal currentBalance = bankAccount.getUser().getBalance();
            if (currentBalance == null) {
                currentBalance = BigDecimal.ZERO;
            }
            bankAccount.getUser().setBalance(currentBalance.add(bankTransfer.getAmount()));
        } else if (TransactionTypeEnum.TransactionType.DEBIT.equals(bankTransferCreateDTO.getType())) {
            if (bankTransferCreateDTO.getAmount() == null) {
                throw new InvalidAmountException("Le montant n'est pas valide.");
            }
            if (bankTransferCreateDTO.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
                throw new NullTransferException("Le montant de la transaction ne doit pas être nul");
            }
            if (bankTransferCreateDTO.getAmount().compareTo(bankAccount.getUser().getBalance()) > 0 ) {
                throw new InsufficientBalanceException("Solde insuffisant pour le transfer.");
            }
            bankTransfer.setType(TransactionTypeEnum.TransactionType.DEBIT);
            bankAccount.getUser().setBalance(bankAccount.getUser().getBalance().subtract(bankTransfer.getAmount()));
        } else {
            throw new IllegalArgumentException("Type de transaction non valide");
        }

        bankTransferRepository.save(bankTransfer);
    }


    @Override
    public Page<BankTransferInformationDTO> getTransferDetails(Pageable pageable) {
        User user = userRepository.findById(accountService.getCurrentAccount().getId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inexistant"));

        Page<BankTransfer> pageTransfers = bankTransferRepository.findBankTransferByBankAccount_User(user, pageable);

        return pageTransfers.map(bankTransfer -> {
            BankTransferInformationDTO dto = mapper.mapBankTransfer(bankTransfer);
            TransactionTypeEnum.TransactionType type = bankTransfer.getType();

            if (type != null && type == TransactionTypeEnum.TransactionType.DEBIT) {
                dto.setAmount(dto.getAmount().negate());
            }

            return dto;
        });
    }





}
