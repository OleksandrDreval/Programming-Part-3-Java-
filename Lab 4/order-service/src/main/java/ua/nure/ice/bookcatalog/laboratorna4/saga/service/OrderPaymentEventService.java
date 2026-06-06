package ua.nure.ice.bookcatalog.laboratorna4.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.PaymentResultPayload;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.ProcessedEvent;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.ProcessedEventRepository;


@Service
public class OrderPaymentEventService {

    private static final Logger log = LoggerFactory.getLogger(OrderPaymentEventService.class);

    private static final String PAYMENT_RESERVED_EVENT = "PaymentReserved";
    private static final String PAYMENT_REJECTED_EVENT = "PaymentRejected";

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public OrderPaymentEventService(OrderRepository orderRepository,
                                    ProcessedEventRepository processedEventRepository,
                                    ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    
    @Transactional
    public void handlePaymentResult(EventMessage message) {
        String eventId = message.getEventId();

        if (processedEventRepository.existsByEventId(eventId)) {
            log.info("Event {} already processed, skipping", eventId);
            return;
        }

        PaymentResultPayload payload = deserializePayload(message.getPayload());

        BookOrder order = orderRepository.findById(payload.getOrderId())
                .orElseThrow(() -> new IllegalStateException(
                        "Order " + payload.getOrderId() + " not found for event " + eventId));

        String eventType = message.getEventType();

        if (PAYMENT_RESERVED_EVENT.equals(eventType)) {
            order.confirm();
            log.info("Order {} confirmed (payment {})", order.getId(), payload.getPaymentId());
        } else if (PAYMENT_REJECTED_EVENT.equals(eventType)) {
            order.cancel();
            log.info("Order {} cancelled (payment {})", order.getId(), payload.getPaymentId());
        } else {
            log.warn("Unknown payment event type: {}", eventType);
            return;
        }

        orderRepository.save(order);
        processedEventRepository.save(new ProcessedEvent(eventId));
    }

    private PaymentResultPayload deserializePayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, PaymentResultPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize payment result payload", e);
        }
    }
}
