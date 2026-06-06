package ua.nure.ice.bookcatalog.laboratorna4.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.CreateSagaOrderRequest;
import ua.nure.ice.bookcatalog.laboratorna4.saga.service.OrderApplicationService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaOrderControllerTest {

    @Mock
    private OrderApplicationService orderApplicationService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private SagaOrderController controller;

    private BookOrder testOrder;
    private CreateSagaOrderRequest request;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Book", "Author", 2023, BookGenre.FICTION, new BigDecimal("500.00"));
        testOrder = new BookOrder.Builder(1L, "Alice")
                .addBook(book)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
        request = new CreateSagaOrderRequest("Alice", List.of(1L));
    }

    @Test
    void createOrder_valid_shouldReturnCreated() {
        when(orderApplicationService.createOrderWithIdempotency(anyString(), anyString(), anyList()))
                .thenReturn(testOrder);

        ResponseEntity<?> response = controller.createOrder("key-001", request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createOrder_illegalArgument_shouldReturnBadRequest() {
        when(orderApplicationService.createOrderWithIdempotency(anyString(), anyString(), anyList()))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        ResponseEntity<?> response = controller.createOrder("key-002", request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createOrder_illegalState_shouldReturnConflict() {
        when(orderApplicationService.createOrderWithIdempotency(anyString(), anyString(), anyList()))
                .thenThrow(new IllegalStateException("Conflict"));

        ResponseEntity<?> response = controller.createOrder("key-003", request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void getAllOrders_shouldReturnOkWithOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        ResponseEntity<List<BookOrder>> response = controller.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllOrders_emptyList_shouldReturnOkWithEmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<BookOrder>> response = controller.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getOrderById_found_shouldReturnOk() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        ResponseEntity<BookOrder> response = controller.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getOrderById_notFound_shouldReturnNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<BookOrder> response = controller.getOrderById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
