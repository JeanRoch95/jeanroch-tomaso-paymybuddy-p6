package com.paymybuddy.repository;

import com.paymybuddy.model.BankTransfer;
import org.springframework.data.repository.CrudRepository;

public interface BankTransferRepository extends CrudRepository<BankTransfer, Long> {
}
