package ua.nure.ice.bookcatalog.laboratorna4.saga.service;

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
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.BookGenre;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderCreationService orderCreationService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book(1L, "Test Book", "Test Author", 2023, BookGenre.FICTION, new BigDecimal("500.00"));
    }

    @Test
    void createNewOrder_withValidBooks_shouldCreateOrderAndOutboxEvent() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(invocation -> {
            BookOrder order = invocation.getArgument(0);
            return new BookOrder.Builder(1L, order.getCustomerName())
                    .books(order.getBooks())
                    .status(order.getStatus())
                    .build();
        });

        BookOrder result = orderCreationService.createNewOrder("Alice", List.of(1L), "test-key-001");

        assertNotNull(result);
        assertEquals("Alice", result.getCustomerName());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());

        verify(orderRepository).save(any(BookOrder.class));
        verify(idempotencyKeyRepository).save(any(IdempotencyKey.class));
        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void createNewOrder_shouldSaveOutboxEventWithCorrectType() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(invocation -> {
            BookOrder order = invocation.getArgument(0);
            return new BookOrder.Builder(1L, order.getCustomerName())
                    .books(order.getBooks())
                    .status(order.getStatus())
                    .build();
        });

        orderCreationService.createNewOrder("Alice", List.of(1L), "key-002");

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        OutboxEvent savedEvent = outboxCaptor.getValue();
        assertEquals("OrderCreated", savedEvent.getEventType());
        assertNotNull(savedEvent.getEventId());
        assertNotNull(savedEvent.getCorrelationId());
        assertNotNull(savedEvent.getPayload());
    }

    @Test
    void createNewOrder_shouldSerializePayloadWithCorrectAmount() throws JsonProcessingException {
        Book expensiveBook = new Book(2L, "Expensive", "Author", 2024, BookGenre.SCIENCE_FICTION, new BigDecimal("1500.00"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(expensiveBook));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(invocation -> {
            BookOrder order = invocation.getArgument(0);
            return new BookOrder.Builder(1L, order.getCustomerName())
                    .books(order.getBooks())
                    .status(order.getStatus())
                    .build();
        });

        orderCreationService.createNewOrder("Bob", List.of(1L, 2L), "key-003");

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        OrderCreatedPayload payload = objectMapper.readValue(
                outboxCaptor.getValue().getPayload(), OrderCreatedPayload.class);
        assertEquals(new BigDecimal("2000.00"), payload.getAmount());
        assertEquals("Bob", payload.getCustomerName());
    }

    @Test
    void createNewOrder_withEmptyBookIds_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> orderCreationService.createNewOrder("Alice", List.of(), "key-004"));
    }

    @Test
    void createNewOrder_withNonExistentBook_shouldThrowException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> orderCreationService.createNewOrder("Alice", List.of(999L), "key-005"));
    }

    @Test
    void createNewOrder_shouldSaveIdempotencyKeyWithCorrectOrderId() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(invocation -> {
            BookOrder order = invocation.getArgument(0);
            return new BookOrder.Builder(42L, order.getCustomerName())
                    .books(order.getBooks())
                    .status(order.getStatus())
                    .build();
        });

        orderCreationService.createNewOrder("Alice", List.of(1L), "idem-key-100");

        ArgumentCaptor<IdempotencyKey> keyCaptor = ArgumentCaptor.forClass(IdempotencyKey.class);
        verify(idempotencyKeyRepository).save(keyCaptor.capture());

        assertEquals("idem-key-100", keyCaptor.getValue().getIdempotencyKey());
        assertEquals(42L, keyCaptor.getValue().getOrderId());
    }
}
