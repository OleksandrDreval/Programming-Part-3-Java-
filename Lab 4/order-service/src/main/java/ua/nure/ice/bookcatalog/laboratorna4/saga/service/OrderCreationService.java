package ua.nure.ice.bookcatalog.laboratorna4.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna4.repository.BookRepository;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.OrderCreatedPayload;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.IdempotencyKey;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.IdempotencyKeyRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.OutboxEventRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class OrderCreationService {

    private static final Logger log = LoggerFactory.getLogger(OrderCreationService.class);
    private static final String ORDER_CREATED_EVENT_TYPE = "OrderCreated";

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    public OrderCreationService(OrderRepository orderRepository,
                                BookRepository bookRepository,
                                OutboxEventRepository outboxEventRepository,
                                IdempotencyKeyRepository idempotencyKeyRepository,
                                ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.objectMapper = objectMapper;
    }

    
    @Transactional
    public BookOrder createNewOrder(String customerName, List<Long> bookIds, String idempotencyKeyValue) {
        List<Book> books = bookIds.stream()
                .map(bookId -> bookRepository.findById(bookId)
                        .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId)))
                .toList();

        if (books.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one book.");
        }

        BigDecimal totalAmount = books.stream()
                .map(Book::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BookOrder newOrder = new BookOrder.Builder(customerName)
                .books(books)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        BookOrder savedOrder = orderRepository.save(newOrder);

        String sagaId = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();

        idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKeyValue, savedOrder.getId()));

        OrderCreatedPayload payload = new OrderCreatedPayload(
                savedOrder.getId(), customerName, totalAmount);

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEvent outboxEvent = new OutboxEvent(eventId, ORDER_CREATED_EVENT_TYPE, payloadJson, sagaId);
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OrderCreated payload", e);
        }

        log.info("Created order {} with sagaId {} and amount {}", savedOrder.getId(), sagaId, totalAmount);
        return savedOrder;
    }
}
