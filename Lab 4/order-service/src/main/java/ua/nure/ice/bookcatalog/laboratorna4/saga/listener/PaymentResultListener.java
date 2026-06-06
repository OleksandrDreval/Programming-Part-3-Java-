package ua.nure.ice.bookcatalog.laboratorna4.saga.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.saga.service.OrderPaymentEventService;


@Component
public class PaymentResultListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);

    private final OrderPaymentEventService orderPaymentEventService;

    public PaymentResultListener(OrderPaymentEventService orderPaymentEventService) {
        this.orderPaymentEventService = orderPaymentEventService;
    }

    @RabbitListener(queues = "order.payment-reserved.queue")
    public void handlePaymentReserved(EventMessage message) {
        log.info("Received PaymentReserved event: {}", message.getEventId());
        orderPaymentEventService.handlePaymentResult(message);
    }

    @RabbitListener(queues = "order.payment-rejected.queue")
    public void handlePaymentRejected(EventMessage message) {
        log.info("Received PaymentRejected event: {}", message.getEventId());
        orderPaymentEventService.handlePaymentResult(message);
    }
}
