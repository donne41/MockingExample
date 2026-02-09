package com.example.payment;

import java.math.BigDecimal;

public interface PaymentApi {
    PaymentResponse charge(String apiKey, BigDecimal amount);
}
