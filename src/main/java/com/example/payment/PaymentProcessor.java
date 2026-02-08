package com.example.payment;

public class PaymentProcessor {
    private static final String API_KEY = "sk_test_123456";
    private PaymentApi paymentApi;
    private DatabaseRepo databaseRepo;
    private EmailService emailService;


    public PaymentProcessor(PaymentApi paymentAPI, DatabaseRepo dbRepo, EmailService emailService) {
        this.paymentApi = paymentAPI;
        this.databaseRepo = dbRepo;
        this.emailService = emailService;
    }


    public boolean processPayment(double amount) {
        // Anropar extern betaltjänst direkt med statisk API-nyckel
        PaymentResponse response = paymentApi.charge(API_KEY, amount);

        // Skriver till databas direkt
        if (response.isSuccess()) {
            databaseRepo
                    .executeUpdate("INSERT INTO payments (amount, status) VALUES (" + amount + ", 'SUCCESS')");
        }

        // Skickar e-post direkt
        if (response.isSuccess()) {
            emailService.sendPaymentConfirmation("user@example.com", amount);
        }

        return response.isSuccess();
    }
}
