package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.model.BankTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BankTransferService {

    Double getUserBalance();

    void creditFromBankAccount(BankTransferDTO bankTransferDTO);

    void debitFromBankAccount(BankTransferDTO bankTransferDTO);

    Page<BankTransferDTO> getTransferForUser(Pageable pageable);

    Page<BankTransferInformationDTO> getTransferDetails(Pageable pageable);

}
