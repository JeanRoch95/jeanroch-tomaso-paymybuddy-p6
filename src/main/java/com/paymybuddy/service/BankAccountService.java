package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankAccountService {

    /**
     * Creates a new bank that is associated with a user
     *
     * @param iban The IBAN of the bank that needs to be added
     * @param swift The SWIFT of the bank that needs to be added
     * @param name the NAME of the bank thats needs to be added
     * @return The new bank object.
     */
    BankAccount addBankAccount(BankAccountDTO bankAccountDTO);

    Iterable<BankAccount> getBankAccountByCurrentUserId();

    Page<BankAccount> getSortedBankAccountByCurrentUserId(Pageable pageable);

    /**
     * Send money to the user's bank account
     *
     * @param bankTransferDTO contain all necessary information about transfer
     */
    void addMoneyToAccount(BankTransferDTO bankTransferDTO, Integer id);

    void sendMoneyToBank(BankTransferDTO bankTransferDTO, Integer id);

}
