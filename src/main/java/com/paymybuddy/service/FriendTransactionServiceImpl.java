package com.paymybuddy.service;

import com.paymybuddy.constant.Fee;
import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.FriendTransactionInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.FriendTransactionMapper;
import com.paymybuddy.model.FriendTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import com.paymybuddy.repository.FriendTransactionRepository;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendTransactionServiceImpl implements FriendTransactionService {

    private FriendTransactionMapper mapper;

    private UserRepository userRepository;

    private FriendTransactionRepository friendTransactionRepository;

    private UserConnectionRepository userConnectionRepository;

    public FriendTransactionServiceImpl(FriendTransactionMapper mapper, UserRepository userRepository, FriendTransactionRepository friendTransactionRepository, UserConnectionRepository userConnectionRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.friendTransactionRepository = friendTransactionRepository;
        this.userConnectionRepository = userConnectionRepository;
    }

    @Override
    public Double getCurrentUserBalance() {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Double currentUserBalance = user.get().getBalance();
        return currentUserBalance;
    }

    public void sendMoneyToFriend(FriendTransactionInformationDTO friendTransactionInformationDTO) {
        Optional<User> senderUser = userRepository.findById(SecurityUtils.getCurrentUserId());

        Optional<User> receiverUser = userRepository.findById(Math.toIntExact(friendTransactionInformationDTO.getReceiverUserId()));

        Optional<UserConnection> userConnection = userConnectionRepository.findBySenderAndReceiver(senderUser.get(), receiverUser.get());

        if (senderUser.get().getBalance() < calculateFinalPrice(friendTransactionInformationDTO.getAmount())) {
            throw new InsufficientBalanceException("Vous ne disposez pas des fond nécessaires");
        }

        if (friendTransactionInformationDTO.getAmount() <= 0) {
            throw new NullTransferException("Le montant ne peux être null ou inférieur a 0€");
        }

        FriendTransaction friendTransaction = new FriendTransaction();
        friendTransaction.setSender(userConnection.get().getSender());
        friendTransaction.setReceiver(userConnection.get().getReceiver());
        friendTransaction.setAmount(friendTransactionInformationDTO.getAmount());
        friendTransaction.setDescription(friendTransactionInformationDTO.getDescription());
        friendTransaction.setCreatedAt(Instant.now().plus(2, ChronoUnit.HOURS));
        friendTransaction.setFees(friendTransaction.getAmount() * Fee.FRIEND_TRANSACTION_FEES);

        senderUser.get().setBalance(getCurrentUserBalance() - calculateFinalPrice(friendTransaction.getAmount()));
        receiverUser.get().setBalance(receiverUser.get().getBalance() + friendTransaction.getAmount());

        friendTransactionRepository.save(friendTransaction);
    }

    @Override
    public Double calculateFinalPrice(Double amount) {
        return (amount + (amount * Fee.FRIEND_TRANSACTION_FEES));
    }

    @Override
    public Double calculateMaxPrice(Double balance) {
        return (balance - (balance * Fee.FRIEND_TRANSACTION_FEES));
    }

    @Override
    public Page<FriendTransactionDisplayDTO> getTransactionsForUser(Pageable pageable) {
        Optional<User> user = userRepository.findById(SecurityUtils.getCurrentUserId());
        Page<FriendTransaction> sentTransactions = friendTransactionRepository.findBySenderOrderByCreatedAtDesc(user.get(), pageable);
        Page<FriendTransaction> receivedTransactions = friendTransactionRepository.findByReceiverOrderByCreatedAtDesc(user.get(), pageable);

        List<FriendTransactionDisplayDTO> sentDTOs = sentTransactions.getContent().stream()
                .map(transaction -> {
                    FriendTransactionDisplayDTO dto = mapper.toFriendTransactionDisplayDTO(transaction);
                    dto.setAmount(-dto.getAmount());
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
        sentDTOs.sort(Comparator.comparing(FriendTransactionDisplayDTO::getCreatedAt).reversed());

        return new PageImpl<>(sentDTOs, pageable, sentDTOs.size());
    }

}
