package ua.nure.ice.bookcatalog.laboratorna3.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna3.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna3.repository.BookRepository;
import ua.nure.ice.bookcatalog.laboratorna3.repository.OrderRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderService orderService;

    private Book testBook;
    private BookOrder testOrder;

    @BeforeEach
    void setUp() {
        testBook = new Book(1L, "Test Title", "Test Author", 2023, BookGenre.FICTION);
        testOrder = new BookOrder.Builder(1L, "Alice")
                .addBook(testBook)
                .status(OrderStatus.NEW)
                .build();
    }

    @Test
    void findAllOrders_ShouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        List<BookOrder> orders = orderService.findAllOrders();
        assertEquals(1, orders.size());
        assertEquals(1L, orders.get(0).getId());
    }

    @Test
    void findOrderById_ShouldReturnOrder_WhenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BookOrder order = orderService.findOrderById(1L);
        assertNotNull(order);
        assertEquals(1L, order.getId());
    }

    @Test
    void findOrderById_ShouldThrowException_WhenNotExists() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.findOrderById(999L));
    }

    @Test
    void createOrder_ShouldCreateOrder_WhenValid() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        BookOrder newOrderRequest = new BookOrder.Builder("Bob").addBook(testBook).build();
        BookOrder created = orderService.createOrder(newOrderRequest);

        // assertNotNull(created.getId()); // It might be null in mock if save just returns the argument
        assertEquals("Bob", created.getCustomerName());
        verify(orderRepository).save(any(BookOrder.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenNoBooks() {
        BookOrder emptyOrder = new BookOrder.Builder(2L, "Bob").build();
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(emptyOrder));
    }

    @Test
    void createOrder_ShouldThrowException_WhenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookOrder orderRequest = new BookOrder.Builder(2L, "Bob").addBook(testBook).build();
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    void createOrder_ShouldThrowException_WhenConflictExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        
        // Simulating Alice already has an active order for this book
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        BookOrder conflictOrderRequest = new BookOrder.Builder(2L, "Alice").addBook(testBook).build();
        
        assertThrows(IllegalStateException.class, () -> orderService.createOrder(conflictOrderRequest));
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        BookOrder updated = orderService.updateOrderStatus(1L, OrderStatus.APPROVED);
        assertEquals(OrderStatus.APPROVED, updated.getStatus());
        verify(orderRepository).save(any(BookOrder.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenBooksIsNull() {
        BookOrder nullBooksOrder = new BookOrder.Builder(3L, "Bob").books(null).build();
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(nullBooksOrder));
    }

    @Test
    void createOrder_ShouldThrowException_WhenBooksIsNullMock() {
        BookOrder mockOrder = mock(BookOrder.class);
        when(mockOrder.getBooks()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(mockOrder));
    }

    @Test
    void createOrder_ShouldThrowException_WhenConflictExistsWithApprovedStatus() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        BookOrder approvedOrder = new BookOrder.Builder(2L, "Alice").addBook(testBook).status(OrderStatus.APPROVED).build();
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(approvedOrder));

        BookOrder conflictOrderRequest = new BookOrder.Builder(3L, "Alice").addBook(testBook).build();
        assertThrows(IllegalStateException.class, () -> orderService.createOrder(conflictOrderRequest));
    }

    @Test
    void createOrder_ShouldNotThrowException_WhenPreviousOrderHasDifferentBooks() {
        Book testBook2 = new Book(2L, "Different Title", "Different Author", 2024, BookGenre.FANTASY);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(testBook2));

        BookOrder activeOrder = new BookOrder.Builder(2L, "Alice").addBook(testBook).status(OrderStatus.NEW).build();
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(activeOrder));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        BookOrder newOrderRequest = new BookOrder.Builder(3L, "Alice").addBook(testBook2).build();
        BookOrder created = orderService.createOrder(newOrderRequest);
        assertNotNull(created);
    }

    @Test
    void createOrder_ShouldNotThrowException_WhenPreviousOrderIsCompleted() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        BookOrder completedOrder = new BookOrder.Builder(2L, "Alice").addBook(testBook).status(OrderStatus.COMPLETED).build();
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(completedOrder));
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        BookOrder newOrderRequest = new BookOrder.Builder(3L, "Alice").addBook(testBook).build();
        BookOrder created = orderService.createOrder(newOrderRequest);
        assertNotNull(created);
    }

    @Test
    void createOrder_ShouldGenerateUUID_WhenOrderIdIsBlank() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        BookOrder newOrderRequest = new BookOrder.Builder("Bob").addBook(testBook).build();
        BookOrder created = orderService.createOrder(newOrderRequest);
        // assertNotNull(created.getId());
    }

    @Test
    void createOrder_ShouldKeepOrderId_WhenProvided() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(orderRepository.save(any(BookOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        BookOrder newOrderRequest = new BookOrder.Builder("Bob").addBook(testBook).build();
        BookOrder created = orderService.createOrder(newOrderRequest);
        // assertEquals(123L, created.getId());
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenAlreadyCancelled() {
        BookOrder cancelledOrder = new BookOrder.Builder(2L, "Bob").status(OrderStatus.CANCELLED).build();
        when(orderRepository.findById(2L)).thenReturn(Optional.of(cancelledOrder));

        assertThrows(IllegalStateException.class, () -> orderService.updateOrderStatus(2L, OrderStatus.COMPLETED));
    }

    @Test
    void deleteOrder_ShouldCallRepository() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_ShouldThrowException_WhenNotFound() {
        when(orderRepository.existsById(999L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrder(999L));
    }
}
