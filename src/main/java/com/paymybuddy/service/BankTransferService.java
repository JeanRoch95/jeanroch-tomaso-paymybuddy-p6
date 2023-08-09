package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BankTransferService {

    Double getUserBalance();

    void creditFromBankAccount(BankTransferCreateDTO bankTransferCreateDTO);

    void debitFromBankAccount(BankTransferCreateDTO bankTransferCreateDTO);

    Page<BankTransferCreateDTO> getTransferForUser(Pageable pageable);

    Page<BankTransferInformationDTO> getTransferDetails(Pageable pageable);

}
