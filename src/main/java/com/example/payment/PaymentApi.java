package com.example.payment;

public interface PaymentApi {
    PaymentResponse charge(String apiKey, double amount);
}
