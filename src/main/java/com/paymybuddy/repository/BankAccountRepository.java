package com.paymybuddy.repository;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    BankAccount findByIbanAndUser_Id(String iban, int id);

    Iterable<BankAccount> findByUserId(int id);

    Page<BankAccount> findByUserIdOrderByCreatedAtDesc(int id, Pageable pageable);
}
