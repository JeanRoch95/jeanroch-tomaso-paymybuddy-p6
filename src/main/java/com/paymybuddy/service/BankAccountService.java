package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferDisplayDTO;
import com.paymybuddy.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankAccountService {

    BankAccountDTO addBankAccount(BankTransferDisplayDTO bankTransferDisplayDTO); // TODO Utiliser DTO

    Iterable<BankAccountDTO> getBankAccountByCurrentUserId();

    Page<BankAccountDTO> getSortedBankAccountByCurrentUserId(Pageable pageable);
}
