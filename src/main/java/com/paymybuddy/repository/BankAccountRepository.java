package com.paymybuddy.repository;

import com.paymybuddy.model.BankAccount;
import org.springframework.data.repository.CrudRepository;

public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {
    BankAccount findByIban(String iban);
}
