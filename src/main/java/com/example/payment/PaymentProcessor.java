package com.example.payment;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentProcessor {
    private static final String API_KEY = "sk_test_123456";
    private String userEmail = "user@example.com";
    private PaymentApi paymentApi;
    private DatabaseRepo databaseRepo;
    private EmailService emailService;


    public PaymentProcessor(PaymentApi paymentAPI, DatabaseRepo dbRepo, EmailService emailService) {
        this.paymentApi = paymentAPI;
        this.databaseRepo = dbRepo;
        this.emailService = emailService;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean processPayment(double amount) {
        // Anropar extern betaltjänst direkt med statisk API-nyckel
        PaymentResponse response = paymentApi.charge(API_KEY, amount);

        // Skriver till databas direkt
        if (response.isSuccess()) {
            try {
                PreparedStatement sql = databaseRepo.prepareStatement("INSERT INTO payments (amount, status) VALUES ( ?, 'SUCCESS')");
                sql.setDouble(1, amount);
                databaseRepo
                        .executeUpdate(sql);
            } catch (SQLException e) {
                throw new IllegalArgumentException("Error with prepared statement");
            }
        }

        // Skickar e-post direkt
        if (response.isSuccess()) {
            emailService.sendPaymentConfirmation(userEmail, amount);
        }

        return response.isSuccess();
    }
}
