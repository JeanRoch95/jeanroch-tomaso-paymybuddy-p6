package com.paymybuddy.service;

import com.paymybuddy.model.Bank;

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

}
