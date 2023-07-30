package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.model.BankTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BankTransferService {

    Double getUserBalance();

    void creditFromBankAccount(BankTransferDTO bankTransferDTO);

    void debitFromBankAccount(BankTransferDTO bankTransferDTO);

    Page<BankTransfer> getTransferForUser(Pageable pageable);

    Page<TransactionDTO> getTransferDetails(Pageable pageable);

}
