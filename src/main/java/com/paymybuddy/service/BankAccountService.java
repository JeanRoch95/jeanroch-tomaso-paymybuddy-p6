package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankAccountService {

    BankAccountDTO addBankAccount(BankAccountCreateDTO bankAccountCreateDTO);

    Iterable<BankAccountDTO> getBankAccountByCurrentUserId();

    Page<BankAccountDTO> getSortedBankAccountByCurrentUserId(Pageable pageable);
}
