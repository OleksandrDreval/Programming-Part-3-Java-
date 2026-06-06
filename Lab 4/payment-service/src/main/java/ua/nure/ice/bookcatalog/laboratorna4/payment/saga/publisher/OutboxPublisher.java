package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.config.RabbitMqConfig;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEventStatus;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository.OutboxEventRepository;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, RabbitTemplate rabbitTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW);

        for (OutboxEvent event : pendingEvents) {
            EventMessage message = new EventMessage(
                    event.getEventId(),
                    event.getEventType(),
                    event.getCorrelationId(),
                    event.getPayload()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.SAGA_EXCHANGE,
                    event.getEventType(),
                    message
            );

            event.markPublished();
            outboxEventRepository.save(event);

            log.info("Published event {} of type {} with correlationId {}",
                    event.getEventId(), event.getEventType(), event.getCorrelationId());
        }
    }
}
