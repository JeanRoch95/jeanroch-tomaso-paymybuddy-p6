package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountInformationDTO;
import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankAccountService {

    BankAccount addBankAccount(BankAccountInformationDTO bankAccountInformationDTO);

    Iterable<BankAccountDTO> getBankAccountByCurrentUserId();

    Page<BankAccountDTO> getSortedBankAccountByCurrentUserId(Pageable pageable);
}
