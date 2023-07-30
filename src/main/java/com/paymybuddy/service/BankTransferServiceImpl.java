package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.TransactionMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.CheckSufficientBalance;
import com.paymybuddy.utils.SecurityUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BankTransferServiceImpl implements BankTransferService{

    TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    private UserRepository userRepository;

    private BankAccountRepository bankAccountRepository;

    private BankTransferRepository bankTransferRepository;

    private BankAccountService bankAccountService;


    public BankTransferServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository, BankTransferRepository bankTransferRepository, BankAccountService bankAccountService) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransferRepository = bankTransferRepository;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public Double getUserBalance() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Double balance = user.get().getBalance();
        return balance;
    }

    @Override
    public void creditFromBankAccount(BankTransferDTO bankTransferDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIbanAndUser_Id(bankTransferDTO.getIban(), SecurityUtils.getCurrentUserId());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setType("credit");


        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() + bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

    @Override
    public void debitFromBankAccount(BankTransferDTO bankTransferDTO) {

        BankAccount bankAccount = bankAccountRepository.findByIbanAndUser_Id(bankTransferDTO.getIban(), SecurityUtils.getCurrentUserId());

        if (bankTransferDTO.getAmount() <= 0) {
            throw new NullTransferException("Le montant de la transaction ne doit pas être nul");
        }

        CheckSufficientBalance.checkSufficientBalance(bankAccount, bankTransferDTO.getAmount());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setAmount(bankTransferDTO.getAmount());
        bankTransfer.setDescription(bankTransferDTO.getDescription());
        bankTransfer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bankTransfer.setType("debit");
        bankAccount.getUser().setBalance(bankAccount.getUser().getBalance() - bankTransfer.getAmount());

        bankTransferRepository.save(bankTransfer);
    }

    @Override
    public Page<BankTransfer> getTransferForUser(Pageable pageable) {
        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inexistant"));

        return bankTransferRepository.findByBankAccount_User(user, pageable);
    }

    @Override
    public Page<TransactionDTO> getTransferDetails(Pageable pageable) {
        User user = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inexistant"));

        Page<BankTransfer> pageTransfers = bankTransferRepository.findBankTransferByBankAccount_User(user, pageable);

        return pageTransfers.map(bankTransfer -> {
            String type = bankTransfer.getType();
            return (type != null && type.equalsIgnoreCase("debit"))
                    ? mapper.debitFromBankTransfer(bankTransfer)
                    : mapper.creditFromBankTransfer(bankTransfer);
        });
    }


}
