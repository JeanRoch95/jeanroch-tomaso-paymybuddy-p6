package com.paymybuddy.service.impl;

import com.paymybuddy.constant.Fee;
import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.FriendTransactionCreateDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.mapper.FriendTransactionMapper;
import com.paymybuddy.model.FriendTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.repository.FriendTransactionRepository;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import com.paymybuddy.service.FriendTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendTransactionServiceImpl implements FriendTransactionService {

    private final FriendTransactionMapper mapper;

    private final UserRepository userRepository;

    private final FriendTransactionRepository friendTransactionRepository;

    private final UserConnectionRepository userConnectionRepository;

    private final AccountService accountService;

    private final BalanceService balanceService;

    public FriendTransactionServiceImpl(FriendTransactionMapper mapper, UserRepository userRepository, FriendTransactionRepository friendTransactionRepository, UserConnectionRepository userConnectionRepository, AccountService accountService, BalanceService balanceService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.friendTransactionRepository = friendTransactionRepository;
        this.userConnectionRepository = userConnectionRepository;
        this.accountService = accountService;
        this.balanceService = balanceService;
    }

    @Transactional
    public void sendMoneyToFriend(FriendTransactionCreateDTO friendTransactionCreateDTO) {
        Optional<User> senderUser = userRepository.findById(accountService.getCurrentAccount().getId().intValue());
        Optional<User> receiverUser = userRepository.findById((friendTransactionCreateDTO.getReceiverUserId().intValue()));

        if (friendTransactionCreateDTO.getAmount() == null ) {
            throw new InvalidAmountException("Le montant n'est pas valide");
        }
        if (senderUser.get().getBalance().compareTo(balanceService.calculateFinalPrice(friendTransactionCreateDTO.getAmount())) < 0 ) {
            throw new InsufficientBalanceException("Vous ne disposez pas des fond nécessaires");
        }
        if (friendTransactionCreateDTO.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new NullTransferException("Le montant ne peux être null ou inférieur a 0€");
        }

        Optional<UserConnection> userConnection = userConnectionRepository.findBySenderAndReceiver(senderUser.get(), receiverUser.get());

        FriendTransaction friendTransaction = new FriendTransaction();
        friendTransaction.setSender(userConnection.get().getSender());
        friendTransaction.setReceiver(userConnection.get().getReceiver());
        friendTransaction.setAmount(friendTransactionCreateDTO.getAmount());
        friendTransaction.setDescription(friendTransactionCreateDTO.getDescription());
        friendTransaction.setCreatedAt(Instant.now());
        friendTransaction.setFees(friendTransaction.getAmount().multiply(Fee.FRIEND_TRANSACTION_FEES));

        senderUser.get().setBalance(balanceService.getCurrentUserBalance().subtract(balanceService.calculateFinalPrice(friendTransaction.getAmount())));
        receiverUser.get().setBalance(receiverUser.get().getBalance().add(friendTransaction.getAmount()));

        friendTransactionRepository.save(friendTransaction);
    }

    @Override
    public Page<FriendTransactionDisplayDTO> getTransactionsForUser(Pageable pageable) {
        Optional<User> user = userRepository.findById(accountService.getCurrentAccount().getId().intValue());
        Page<FriendTransaction> sentTransactions = friendTransactionRepository.findBySenderOrderByCreatedAtDesc(user.get(), pageable);
        Page<FriendTransaction> receivedTransactions = friendTransactionRepository.findByReceiverOrderByCreatedAtDesc(user.get(), pageable);

        List<FriendTransactionDisplayDTO> sentDTOs = sentTransactions.getContent().stream()
                .map(transaction -> {
                    FriendTransactionDisplayDTO dto = mapper.toFriendTransactionDisplayDTO(transaction);
                    dto.setAmount(dto.getAmount().negate());
                    dto.setName(transaction.getReceiver().getFirstName());
                    return dto;
                })
                .collect(Collectors.toList());

        List<FriendTransactionDisplayDTO> receivedDTOs = receivedTransactions.getContent().stream()
                .map(transaction -> {
                    FriendTransactionDisplayDTO dto = mapper.toFriendTransactionDisplayDTO(transaction);
                    dto.setName(transaction.getSender().getFirstName());
                    return dto;
                })
                .collect(Collectors.toList());

        sentDTOs.addAll(receivedDTOs);
        sentDTOs.sort(Comparator.comparing(FriendTransactionDisplayDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        return new PageImpl<>(sentDTOs, pageable, sentDTOs.size());
    }

}
