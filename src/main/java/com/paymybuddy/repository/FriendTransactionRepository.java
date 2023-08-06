package com.paymybuddy.repository;

import com.paymybuddy.model.FriendTransaction;
import com.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface FriendTransactionRepository extends CrudRepository<FriendTransaction, Long> {

    Page<FriendTransaction> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);

    Page<FriendTransaction> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);

}
