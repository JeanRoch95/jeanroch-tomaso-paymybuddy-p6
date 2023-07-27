package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import org.springframework.stereotype.Service;

@Service
public interface BankTransferService {

    Double getUserBalance();

    void addMoneyToAccount(BankTransferDTO bankTransferDTO);

    void sendMoneyToBank(BankTransferDTO bankTransferDTO);
}
