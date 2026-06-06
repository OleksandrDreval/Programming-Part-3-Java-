package ua.nure.ice.bookcatalog.laboratorna4.saga.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.IdempotencyKey;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.IdempotencyKeyRepository;

import java.util.List;
import java.util.Optional;


@Service
public class OrderApplicationService {

    private static final Logger log = LoggerFactory.getLogger(OrderApplicationService.class);

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OrderRepository orderRepository;
    private final OrderCreationService orderCreationService;

    public OrderApplicationService(IdempotencyKeyRepository idempotencyKeyRepository,
                                   OrderRepository orderRepository,
                                   OrderCreationService orderCreationService) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.orderRepository = orderRepository;
        this.orderCreationService = orderCreationService;
    }

    
    @Transactional
    public BookOrder createOrderWithIdempotency(String idempotencyKey, String customerName, List<Long> bookIds) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required.");
        }

        Optional<IdempotencyKey> existingKey = idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey);

        if (existingKey.isPresent()) {
            Long existingOrderId = existingKey.get().getOrderId();
            log.info("Idempotency-Key '{}' already used, returning existing order {}", idempotencyKey, existingOrderId);
            return orderRepository.findById(existingOrderId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Order " + existingOrderId + " referenced by idempotency key not found"));
        }

        return orderCreationService.createNewOrder(customerName, bookIds, idempotencyKey);
    }
}
