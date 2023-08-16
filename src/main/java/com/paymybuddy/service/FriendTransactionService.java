package com.paymybuddy.service;

import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.dto.FriendTransactionCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendTransactionService {

    void sendMoneyToFriend(FriendTransactionCreateDTO friendTransactionCreateDTO);

    Page<FriendTransactionDisplayDTO> getTransactionsForUser(Pageable pageable);

}
