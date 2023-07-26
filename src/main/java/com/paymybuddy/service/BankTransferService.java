package com.paymybuddy.service;

import org.springframework.stereotype.Service;

@Service
public interface BankTransferService {

    Double getUserBalance(int id);
}
