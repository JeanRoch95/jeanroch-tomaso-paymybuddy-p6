package com.paymybuddy.service;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
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


    public BankTransferServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository, BankTransferRepository bankTransferRepository, BankAccountService bankAccountService, BankAccountTransferMapper mapper) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransferRepository = bankTransferRepository;
        this.bankAccountService = bankAccountService;
        this.mapper = mapper;
    }

    @Override
    public Double getUserBalance() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Double balance = user.get().getBalance();
        return balance;
    }

    @Override
    public void creditFromBankAccount(BankTransferCreateDTO bankTransferCreateDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIbanAndUser_Id(bankTransferCreateDTO.getIban(), SecurityUtils.getCurrentUserId());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setAmount(bankTransferCreateDTO.getAmount());
        bankTransfer.setDescription(bankTransferCreateDTO.getDescription());
        bankTransfer.setCreatedAt(Instant.now().plus(2, ChronoUnit.HOURS));
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setType(TransactionTypeEnum.TransactionType.CREDIT);

        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() + bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

    @Override
    public void debitFromBankAccount(BankTransferCreateDTO bankTransferCreateDTO) { // TODO Créer que un service

        BankAccount bankAccount = bankAccountRepository.findByIbanAndUser_Id(bankTransferCreateDTO.getIban(), SecurityUtils.getCurrentUserId()); // TODO Verifier si le bankAccount existe - Ou service

        if (bankTransferCreateDTO.getAmount() <= 0) {
            throw new NullTransferException("Le montant de la transaction ne doit pas être nul");
        }

        if(bankTransferCreateDTO.getAmount() > bankAccount.getUser().getBalance()) {
            throw new InsufficientBalanceException("Solde insuffisant pour le transfer.");
        }

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferCreateDTO.getAmount());
        bankTransfer.setDescription(bankTransferCreateDTO.getDescription());
        bankTransfer.setCreatedAt(Instant.now().plus(2, ChronoUnit.HOURS));
        bankTransfer.setType(TransactionTypeEnum.TransactionType.DEBIT);
        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() - bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

    @Override
    public Page<BankTransferCreateDTO> getTransferForUser(Pageable pageable) { // TODO Supprimer + Test
        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inexistant"));

        Page<BankTransfer> bankTransferPage = bankTransferRepository.findByBankAccount_User(user, pageable);

        Page<BankTransferCreateDTO> bankTransferDtoPage = bankTransferPage.map(bankTransfer -> new BankTransferCreateDTO(bankTransfer));

        return bankTransferDtoPage;
    }


    @Override
    public Page<BankTransferInformationDTO> getTransferDetails(Pageable pageable) {
        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inexistant"));

        Page<BankTransfer> pageTransfers = bankTransferRepository.findBankTransferByBankAccount_User(user, pageable);

        return pageTransfers.map(bankTransfer -> {
            TransactionTypeEnum.TransactionType type = bankTransfer.getType();
            return (type != null && type == TransactionTypeEnum.TransactionType.DEBIT)
                    ? mapper.debitFromBankTransfer(bankTransfer)
                    : mapper.creditFromBankTransfer(bankTransfer);
        });
    }


}
