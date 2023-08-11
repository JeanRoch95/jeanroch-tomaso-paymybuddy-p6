package com.paymybuddy.service;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.BankAccountNotFoundException;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.exceptions.UserAlreadyAddException;
import com.paymybuddy.mapper.BankAccountTransferMapper;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BankTransferServiceImpl implements BankTransferService{

    private BankAccountTransferMapper mapper;

    private UserRepository userRepository;

    private BankAccountRepository bankAccountRepository;

    private BankTransferRepository bankTransferRepository;

    private BankAccountService bankAccountService;

    private UserService userService;


    public BankTransferServiceImpl(BankAccountTransferMapper mapper, UserRepository userRepository, BankAccountRepository bankAccountRepository, BankTransferRepository bankTransferRepository, BankAccountService bankAccountService, UserService userService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransferRepository = bankTransferRepository;
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    @Override
    public Double getUserBalance() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Double balance = user.get().getBalance();
        return balance;
    }

    @Override
    public void processBankTransfer(BankTransferCreateDTO bankTransferCreateDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIbanAndUser_Id(bankTransferCreateDTO.getIban(), userService.getCurrentUser().getId().intValue());

        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Le compte bancaire n'existe pas.");
        }

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferCreateDTO.getAmount());
        bankTransfer.setDescription(bankTransferCreateDTO.getDescription());
        bankTransfer.setCreatedAt(Instant.now());

        if (TransactionTypeEnum.TransactionType.CREDIT.equals(bankTransferCreateDTO.getType())) {
            bankTransfer.setType(TransactionTypeEnum.TransactionType.CREDIT);
            bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() + bankTransfer.getAmount());
        } else if (TransactionTypeEnum.TransactionType.DEBIT.equals(bankTransferCreateDTO.getType())) {
            if (bankTransferCreateDTO.getAmount() <= 0) {
                throw new NullTransferException("Le montant de la transaction ne doit pas être nul");
            }
            if (bankTransferCreateDTO.getAmount() > bankAccount.getUser().getBalance()) {
                throw new InsufficientBalanceException("Solde insuffisant pour le transfer.");
            }
            bankTransfer.setType(TransactionTypeEnum.TransactionType.DEBIT);
            bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() - bankTransfer.getAmount());
        } else {
            throw new IllegalArgumentException("Type de transaction non valide");
        }

        bankTransferRepository.save(bankTransfer);
    }


    @Override
    public Page<BankTransferInformationDTO> getTransferDetails(Pageable pageable) {
        User user = userRepository.findById(userService.getCurrentUser().getId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inexistant"));

        Page<BankTransfer> pageTransfers = bankTransferRepository.findBankTransferByBankAccount_User(user, pageable);

        return pageTransfers.map(bankTransfer -> {
            BankTransferInformationDTO dto = mapper.mapBankTransfer(bankTransfer);
            TransactionTypeEnum.TransactionType type = bankTransfer.getType();

            if (type != null && type == TransactionTypeEnum.TransactionType.DEBIT) {
                dto.setAmount(-dto.getAmount());
            }

            return dto;
        });
    }



}
