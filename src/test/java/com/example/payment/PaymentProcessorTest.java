package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @Mock
    PaymentApi payment;
    @Mock
    EmailService emailService;
    @Mock
    DatabaseRepo databaseRepo;
    @Mock
    PaymentResponse paymentResponse;

    @InjectMocks
    PaymentProcessor processor = new PaymentProcessor(payment, databaseRepo, emailService);


    @Test
    void whenPaymentApiReturnsTrueSoShouldProcessPayment() {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);

        var result = processor.processPayment(200);

        assertThat(result).isTrue();
    }

    @Test
    void whenPaymentIsTrueDatabaseShouldGetQuery() {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);

        processor.processPayment(200);
        Mockito.verify(databaseRepo,
                        Mockito.times(1))
                .executeUpdate(Mockito.anyString());
    }

    @Test
    void whenPaymentIsTrueConfirmationShouldBeCalled() {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);

        processor.processPayment(200);
        Mockito.verify(emailService,
                        Mockito.times(1))
                .sendPaymentConfirmation(Mockito.anyString(), Mockito.anyDouble());
    }

    @Test
    void whenResponseIsfalseDatabaseAndEmailShouldNotBeCalledAndReturnFalse() {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(false);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);

        var result = processor.processPayment(200);

        Mockito.verify(emailService,
                        Mockito.times(0))
                .sendPaymentConfirmation(Mockito.anyString(), Mockito.anyDouble());
        Mockito.verify(databaseRepo,
                        Mockito.times(0))
                .executeUpdate(Mockito.anyString());

        assertThat(result).isFalse();
    }
}