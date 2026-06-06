package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.service.PaymentApplicationService;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final PaymentApplicationService paymentApplicationService;

    public OrderCreatedListener(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @RabbitListener(queues = "payment.order-created.queue")
    public void handleOrderCreated(EventMessage message) {
        log.info("Received OrderCreated event: {}", message.getEventId());
        paymentApplicationService.handleOrderCreated(message);
    }
}
