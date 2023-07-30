package com.paymybuddy.repository;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BankTransferRepository extends CrudRepository<BankTransfer, Long> {

    Page<BankTransfer> findByBankAccount_User(User user, Pageable pageable);

    Page<BankTransfer> findBankTransferByBankAccount_User(User user,Pageable pageable);
}
