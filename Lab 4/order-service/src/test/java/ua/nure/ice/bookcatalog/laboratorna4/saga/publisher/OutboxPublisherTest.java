package ua.nure.ice.bookcatalog.laboratorna4.saga.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ua.nure.ice.bookcatalog.laboratorna4.saga.config.RabbitMqConfig;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.OutboxEventStatus;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.OutboxEventRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    private OutboxEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new OutboxEvent("evt-001", "OrderCreated", "{\"orderId\":1}", "saga-001");
    }

    @Test
    void publishPendingEvents_withPendingEvents_shouldPublishAndMarkPublished() {
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(testEvent));

        outboxPublisher.publishPendingEvents();

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.SAGA_EXCHANGE),
                eq("OrderCreated"),
                any(EventMessage.class));
        assertEquals(OutboxEventStatus.PUBLISHED, testEvent.getStatus());
        verify(outboxEventRepository).save(testEvent);
    }

    @Test
    void publishPendingEvents_noPendingEvents_shouldDoNothing() {
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(Collections.emptyList());

        outboxPublisher.publishPendingEvents();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(EventMessage.class));
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void publishPendingEvents_shouldUseEventTypeAsRoutingKey() {
        OutboxEvent paymentEvent = new OutboxEvent("evt-002", "PaymentReserved", "{}", "saga-002");
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(paymentEvent));

        outboxPublisher.publishPendingEvents();

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.SAGA_EXCHANGE),
                eq("PaymentReserved"),
                any(EventMessage.class));
    }

    @Test
    void publishPendingEvents_shouldPublishCorrectEventMessage() {
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(testEvent));

        outboxPublisher.publishPendingEvents();

        ArgumentCaptor<EventMessage> messageCaptor = ArgumentCaptor.forClass(EventMessage.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), messageCaptor.capture());

        EventMessage published = messageCaptor.getValue();
        assertEquals("evt-001", published.getEventId());
        assertEquals("OrderCreated", published.getEventType());
        assertEquals("saga-001", published.getCorrelationId());
        assertEquals("{\"orderId\":1}", published.getPayload());
    }

    @Test
    void publishPendingEvents_multipleEvents_shouldPublishAll() {
        OutboxEvent event2 = new OutboxEvent("evt-002", "PaymentRejected", "{}", "saga-002");
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(testEvent, event2));

        outboxPublisher.publishPendingEvents();

        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), any(EventMessage.class));
        verify(outboxEventRepository, times(2)).save(any());
        assertEquals(OutboxEventStatus.PUBLISHED, testEvent.getStatus());
        assertEquals(OutboxEventStatus.PUBLISHED, event2.getStatus());
    }
}
