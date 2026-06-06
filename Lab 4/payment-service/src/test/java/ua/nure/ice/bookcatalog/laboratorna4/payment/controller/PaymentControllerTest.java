package ua.nure.ice.bookcatalog.laboratorna4.payment.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ua.nure.ice.bookcatalog.laboratorna4.payment.model.Payment;
import ua.nure.ice.bookcatalog.laboratorna4.payment.model.PaymentStatus;
import ua.nure.ice.bookcatalog.laboratorna4.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void getAllPayments_shouldReturnOkWithPayments() {
        Payment payment = new Payment(1L, new BigDecimal("1000.00"), PaymentStatus.RESERVED);
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        ResponseEntity<List<Payment>> response = paymentController.getAllPayments();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(PaymentStatus.RESERVED, response.getBody().get(0).getStatus());
    }

    @Test
    void getAllPayments_emptyList_shouldReturnOkWithEmptyList() {
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Payment>> response = paymentController.getAllPayments();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
