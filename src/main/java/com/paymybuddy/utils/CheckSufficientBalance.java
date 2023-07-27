package com.paymybuddy.utils;

import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;

import java.math.BigDecimal;

public class CheckSufficientBalance {

    public static void checkSufficientBalance(BankAccount bankAccount, Double transferAmount) {
        Double balance = bankAccount.getUser().getBalance();

        if (balance.compareTo(transferAmount) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour le transfer.");
        }
    }
}
