package com.paymybuddy.repository;

import com.paymybuddy.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    BankAccount findByIban(String iban);

    Iterable<BankAccount> findBankAccountsByUserId(int id);

    Page<BankAccount> findBankAccountsByUserId(int id, Pageable pageable);
}
