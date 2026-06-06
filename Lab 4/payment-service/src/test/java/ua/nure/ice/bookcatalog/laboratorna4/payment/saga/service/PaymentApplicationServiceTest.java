package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.ice.bookcatalog.laboratorna4.payment.model.Payment;
import ua.nure.ice.bookcatalog.laboratorna4.payment.model.PaymentStatus;
import ua.nure.ice.bookcatalog.laboratorna4.payment.repository.PaymentRepository;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.OrderCreatedPayload;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.ProcessedEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository.OutboxEventRepository;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository.ProcessedEventRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PaymentApplicationService service;

    private EventMessage createEventMessage(BigDecimal amount) throws JsonProcessingException {
        OrderCreatedPayload payload = new OrderCreatedPayload();
        payload.setOrderId(1L);
        payload.setCustomerName("Alice");
        payload.setAmount(amount);
        String payloadJson = objectMapper.writeValueAsString(payload);
        return new EventMessage("evt-001", "OrderCreated", "saga-001", payloadJson);
    }

    @Test
    void handleOrderCreated_amountUnderLimit_shouldCreateReservedPayment() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("5000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new Payment(p.getOrderId(), p.getAmount(), p.getStatus());
        });

        service.handleOrderCreated(message);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.RESERVED, paymentCaptor.getValue().getStatus());
        assertEquals(1L, paymentCaptor.getValue().getOrderId());
    }

    @Test
    void handleOrderCreated_amountExactlyAtLimit_shouldCreateReservedPayment() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("10000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new Payment(p.getOrderId(), p.getAmount(), p.getStatus());
        });

        service.handleOrderCreated(message);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.RESERVED, paymentCaptor.getValue().getStatus());
    }

    @Test
    void handleOrderCreated_amountOverLimit_shouldCreateRejectedPayment() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("20000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new Payment(p.getOrderId(), p.getAmount(), p.getStatus());
        });

        service.handleOrderCreated(message);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.REJECTED, paymentCaptor.getValue().getStatus());
    }

    @Test
    void handleOrderCreated_reservedPayment_shouldPublishPaymentReservedEvent() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("1000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new Payment(p.getOrderId(), p.getAmount(), p.getStatus());
        });

        service.handleOrderCreated(message);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());
        assertEquals("PaymentReserved", outboxCaptor.getValue().getEventType());
        assertEquals("saga-001", outboxCaptor.getValue().getCorrelationId());
    }

    @Test
    void handleOrderCreated_rejectedPayment_shouldPublishPaymentRejectedEvent() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("99999.99"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new Payment(p.getOrderId(), p.getAmount(), p.getStatus());
        });

        service.handleOrderCreated(message);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());
        assertEquals("PaymentRejected", outboxCaptor.getValue().getEventType());
    }

    @Test
    void handleOrderCreated_duplicateEvent_shouldSkipProcessing() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("1000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(true);

        service.handleOrderCreated(message);

        verify(paymentRepository, never()).save(any());
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void handleOrderCreated_paymentAlreadyExists_shouldFixEventState() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("1000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(true);

        service.handleOrderCreated(message);

        verify(paymentRepository, never()).save(any());
        verify(outboxEventRepository, never()).save(any());
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    @Test
    void handleOrderCreated_shouldRecordProcessedEvent() throws Exception {
        EventMessage message = createEventMessage(new BigDecimal("1000.00"));
        when(processedEventRepository.existsByEventId("evt-001")).thenReturn(false);
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            return new Payment(p.getOrderId(), p.getAmount(), p.getStatus());
        });

        service.handleOrderCreated(message);

        ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).save(captor.capture());
        assertEquals("evt-001", captor.getValue().getEventId());
    }

    @Test
    void handleOrderCreated_invalidPayloadJson_shouldThrowException() {
        EventMessage badMessage = new EventMessage("evt-bad", "OrderCreated", "saga-bad", "invalid-json");
        when(processedEventRepository.existsByEventId("evt-bad")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.handleOrderCreated(badMessage));
    }
}
