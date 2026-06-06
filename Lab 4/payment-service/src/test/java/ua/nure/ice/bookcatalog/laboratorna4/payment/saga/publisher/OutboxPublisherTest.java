package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.config.RabbitMqConfig;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEventStatus;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository.OutboxEventRepository;

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
        testEvent = new OutboxEvent("evt-001", "PaymentReserved", "{\"orderId\":1,\"paymentId\":10}", "saga-001");
    }

    @Test
    void publishPendingEvents_withPendingEvents_shouldPublishAndMarkPublished() {
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(testEvent));

        outboxPublisher.publishPendingEvents();

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.SAGA_EXCHANGE),
                eq("PaymentReserved"),
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
    void publishPendingEvents_shouldBuildCorrectEventMessage() {
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(testEvent));

        outboxPublisher.publishPendingEvents();

        ArgumentCaptor<EventMessage> captor = ArgumentCaptor.forClass(EventMessage.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), captor.capture());

        EventMessage published = captor.getValue();
        assertEquals("evt-001", published.getEventId());
        assertEquals("PaymentReserved", published.getEventType());
        assertEquals("saga-001", published.getCorrelationId());
    }

    @Test
    void publishPendingEvents_multipleEvents_shouldPublishAll() {
        OutboxEvent event2 = new OutboxEvent("evt-002", "PaymentRejected", "{}", "saga-002");
        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW))
                .thenReturn(List.of(testEvent, event2));

        outboxPublisher.publishPendingEvents();

        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), any(EventMessage.class));
        assertEquals(OutboxEventStatus.PUBLISHED, testEvent.getStatus());
        assertEquals(OutboxEventStatus.PUBLISHED, event2.getStatus());
    }
}
