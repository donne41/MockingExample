package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @Mock
    PreparedStatement mockedStatement;
    @InjectMocks
    PaymentProcessor processor;


    @Test
    void whenPaymentApiReturnsTrueSoShouldProcessPayment() throws SQLException {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);
        Mockito.when(databaseRepo.prepareStatement(Mockito.anyString())).thenReturn(mockedStatement);

        var result = processor.processPayment(200);

        assertThat(result).isTrue();
    }

    @Test
    void whenPaymentIsTrueDatabaseShouldGetQuery() throws SQLException {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);
        Mockito.when(databaseRepo.prepareStatement(Mockito.anyString())).thenReturn(mockedStatement);


        processor.processPayment(200);
        Mockito.verify(databaseRepo,
                        Mockito.times(1))
                .executeUpdate(Mockito.any(PreparedStatement.class));
    }

    @Test
    void whenPaymentIsTrueConfirmationShouldBeCalled() throws SQLException {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);
        Mockito.when(databaseRepo.prepareStatement(Mockito.anyString())).thenReturn(mockedStatement);

        processor.processPayment(200);
        Mockito.verify(emailService,
                        Mockito.times(1))
                .sendPaymentConfirmation("user@example.com", 200);
    }

    @Test
    void preparedstatementGoesWrongInProcessPayment() throws SQLException {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(true);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);
        Mockito.when(databaseRepo.prepareStatement(Mockito.anyString())).thenThrow(new SQLException("Error statement"));

        assertThrows(IllegalArgumentException.class, () ->
                processor.processPayment(200));
    }

    @Test
    void whenResponseIsfalseDatabaseAndEmailShouldNotBeCalledAndReturnFalse() {
        Mockito.when(paymentResponse.isSuccess()).thenReturn(false);
        Mockito.when(payment.charge(Mockito.anyString(), Mockito.anyDouble())).thenReturn(paymentResponse);

        var result = processor.processPayment(200);

        Mockito.verify(emailService,
                        Mockito.times(0))
                .sendPaymentConfirmation("user@example.com", 200);
        Mockito.verify(databaseRepo,
                        Mockito.times(0))
                .executeUpdate(Mockito.any(PreparedStatement.class));

        assertThat(result).isFalse();
    }

}