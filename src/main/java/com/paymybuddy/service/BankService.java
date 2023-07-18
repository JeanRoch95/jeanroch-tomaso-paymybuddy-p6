package com.paymybuddy.service;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.Bank;
import org.springframework.stereotype.Service;

import java.util.List;
public interface BankService {

    /**
     * Creates a new bank that is associated with a user
     *
     * @param iban The IBAN of the bank that needs to be added
     * @param swift The SWIFT of the bank that needs to be added
     * @param name the NAME of the bank thats needs to be added
     * @return The new bank object.
     */
    Bank addBank(String iban, String swift, String name);

    Iterable<Bank> findAllBank(int id);

    /**
     * Send money to the user's bank account
     *
     * @param bankTransferDTO contain all necessary information about transfer
     */
    void addMoneyToAccount(BankTransferDTO bankTransferDTO, Integer id);

    void sendMoneyToBank(BankTransferDTO bankTransferDTO, Integer id);

}
