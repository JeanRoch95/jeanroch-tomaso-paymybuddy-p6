package com.paymybuddy.service.impl;

import com.paymybuddy.constant.Fee;
import com.paymybuddy.service.BalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceServiceImpl implements BalanceService {
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

}
