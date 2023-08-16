package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface BankTransferService {

    BigDecimal getUserBalance(); // TODO Service dédié

    void processBankTransfer(BankTransferCreateDTO bankTransferCreateDTO);

    Page<BankTransferInformationDTO> getTransferDetails(Pageable pageable);

}
