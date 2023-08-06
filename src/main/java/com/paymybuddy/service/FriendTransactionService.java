package com.paymybuddy.service;

import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.FriendTransactionInformationDTO;
import com.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendTransactionService {

    Double getCurrentUserBalance();

    void sendMoneyToFriend(FriendTransactionInformationDTO friendTransactionInformationDTO);

    Double calculateFinalPrice(Double amount);

    Double calculateMaxPrice(Double balance);

    Page<FriendTransactionDisplayDTO> getTransactionsForUser(Pageable pageable);
}
