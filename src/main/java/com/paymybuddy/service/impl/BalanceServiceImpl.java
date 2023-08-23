package com.paymybuddy.service.impl;

import com.paymybuddy.constant.Fee;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final UserRepository userRepository;

    private final AccountService accountService;

    public BalanceServiceImpl(UserRepository userRepository, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal amount) {
        return (amount.add (amount.multiply(Fee.FRIEND_TRANSACTION_FEES)));
    }

    @Override
    public BigDecimal calculateMaxPrice(BigDecimal balance) {
        if (balance == null) {
            return BigDecimal.valueOf(0.0);
        }
        return (balance.subtract(balance.multiply(Fee.FRIEND_TRANSACTION_FEES)));
    }

    @Override
    public BigDecimal getCurrentUserBalance() {
        Optional<User> user = userRepository.findById(accountService.getCurrentAccount().getId().intValue());
        BigDecimal currentUserBalance = user.map(User::getBalance).orElse(BigDecimal.valueOf(0.0));
        return currentUserBalance;
    }

}
