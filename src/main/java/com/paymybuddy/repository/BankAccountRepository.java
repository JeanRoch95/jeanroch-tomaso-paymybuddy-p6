package com.paymybuddy.repository;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    BankAccount findByIbanAndUser_Id(String iban, int id);

    Iterable<BankAccount> findBankAccountsByUserId(int id);

    Page<BankAccount> findBankAccountsByUserId(int id, Pageable pageable);
}
