package com.paymybuddy.service;

import java.math.BigDecimal;

public interface BalanceService {

    BigDecimal calculateFinalPrice(BigDecimal amount);

    BigDecimal calculateMaxPrice(BigDecimal balance);
}
