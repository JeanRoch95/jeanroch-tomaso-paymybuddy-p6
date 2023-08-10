package com.paymybuddy.service;

import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.FriendTransactionCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendTransactionService {

    Double getCurrentUserBalance();

    void sendMoneyToFriend(FriendTransactionCreateDTO friendTransactionCreateDTO);

    Double calculateFinalPrice(Double amount);

    Double calculateMaxPrice(Double balance);

    Page<FriendTransactionDisplayDTO> getTransactionsForUser(Pageable pageable);
}
