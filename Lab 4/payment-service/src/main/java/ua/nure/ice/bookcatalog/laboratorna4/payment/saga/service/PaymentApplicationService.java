package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.ice.bookcatalog.laboratorna4.payment.model.Payment;
import ua.nure.ice.bookcatalog.laboratorna4.payment.model.PaymentStatus;
import ua.nure.ice.bookcatalog.laboratorna4.payment.repository.PaymentRepository;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.OrderCreatedPayload;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.PaymentResultPayload;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.ProcessedEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository.OutboxEventRepository;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository.ProcessedEventRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(PaymentApplicationService.class);

    private static final String PAYMENT_RESERVED_EVENT = "PaymentReserved";
    private static final String PAYMENT_REJECTED_EVENT = "PaymentRejected";
    private static final BigDecimal LIMIT = new BigDecimal("10000.00");

    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public PaymentApplicationService(PaymentRepository paymentRepository,
                                     ProcessedEventRepository processedEventRepository,
                                     OutboxEventRepository outboxEventRepository,
                                     ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.processedEventRepository = processedEventRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handleOrderCreated(EventMessage message) {
        String eventId = message.getEventId();
        
        if (processedEventRepository.existsByEventId(eventId)) {
            log.info("Event {} already processed, skipping", eventId);
            return;
        }

        OrderCreatedPayload payload;
        try {
            payload = objectMapper.readValue(message.getPayload(), OrderCreatedPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OrderCreated payload", e);
        }

        Long orderId = payload.getOrderId();
        if (paymentRepository.existsByOrderId(orderId)) {
            log.warn("Payment for order {} already exists, but event {} was not processed. Fixing event state.", orderId, eventId);
            processedEventRepository.save(new ProcessedEvent(eventId));
            return;
        }

        BigDecimal amount = payload.getAmount();
        PaymentStatus status = amount.compareTo(LIMIT) <= 0 ? PaymentStatus.RESERVED : PaymentStatus.REJECTED;

        Payment payment = new Payment(orderId, amount, status);
        payment = paymentRepository.save(payment);

        String outboxEventType = status == PaymentStatus.RESERVED ? PAYMENT_RESERVED_EVENT : PAYMENT_REJECTED_EVENT;
        String newEventId = UUID.randomUUID().toString();
        PaymentResultPayload resultPayload = new PaymentResultPayload(orderId, payment.getId());

        try {
            String resultPayloadJson = objectMapper.writeValueAsString(resultPayload);
            OutboxEvent outboxEvent = new OutboxEvent(newEventId, outboxEventType, resultPayloadJson, message.getCorrelationId());
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payment result payload", e);
        }

        processedEventRepository.save(new ProcessedEvent(eventId));

        log.info("Processed OrderCreated for order {}. Created payment {} with status {}", orderId, payment.getId(), status);
    }
}
