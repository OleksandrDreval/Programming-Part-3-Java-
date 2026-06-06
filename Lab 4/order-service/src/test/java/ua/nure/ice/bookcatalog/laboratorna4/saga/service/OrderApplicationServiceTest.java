package ua.nure.ice.bookcatalog.laboratorna4.saga.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.IdempotencyKey;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.IdempotencyKeyRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCreationService orderCreationService;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    private BookOrder existingOrder;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Book", "Author", 2023, BookGenre.FICTION, new BigDecimal("100.00"));
        existingOrder = new BookOrder.Builder(1L, "Alice")
                .addBook(book)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
    }

    @Test
    void createOrderWithIdempotency_newKey_shouldDelegateToCreationService() {
        when(idempotencyKeyRepository.findByIdempotencyKey("new-key")).thenReturn(Optional.empty());
        when(orderCreationService.createNewOrder(anyString(), anyList(), anyString())).thenReturn(existingOrder);

        BookOrder result = orderApplicationService.createOrderWithIdempotency("new-key", "Alice", List.of(1L));

        assertNotNull(result);
        verify(orderCreationService).createNewOrder("Alice", List.of(1L), "new-key");
    }

    @Test
    void createOrderWithIdempotency_existingKey_shouldReturnExistingOrder() {
        IdempotencyKey storedKey = new IdempotencyKey("repeat-key", 1L);
        when(idempotencyKeyRepository.findByIdempotencyKey("repeat-key")).thenReturn(Optional.of(storedKey));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        BookOrder result = orderApplicationService.createOrderWithIdempotency("repeat-key", "Alice", List.of(1L));

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderCreationService, never()).createNewOrder(anyString(), anyList(), anyString());
    }

    @Test
    void createOrderWithIdempotency_existingKeyButOrderMissing_shouldThrowIllegalState() {
        IdempotencyKey storedKey = new IdempotencyKey("orphan-key", 999L);
        when(idempotencyKeyRepository.findByIdempotencyKey("orphan-key")).thenReturn(Optional.of(storedKey));
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> orderApplicationService.createOrderWithIdempotency("orphan-key", "Alice", List.of(1L)));
    }

    @Test
    void createOrderWithIdempotency_nullKey_shouldThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> orderApplicationService.createOrderWithIdempotency(null, "Alice", List.of(1L)));
    }

    @Test
    void createOrderWithIdempotency_blankKey_shouldThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> orderApplicationService.createOrderWithIdempotency("   ", "Alice", List.of(1L)));
    }

    @Test
    void createOrderWithIdempotency_emptyKey_shouldThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> orderApplicationService.createOrderWithIdempotency("", "Alice", List.of(1L)));
    }
}
