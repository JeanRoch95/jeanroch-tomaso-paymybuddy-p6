package com.paymybuddy.repository;

import com.paymybuddy.model.Bank;
import org.springframework.data.repository.CrudRepository;

public interface BankRepository extends CrudRepository<Bank, Long> {
    Bank findByIban(String iban);
}
