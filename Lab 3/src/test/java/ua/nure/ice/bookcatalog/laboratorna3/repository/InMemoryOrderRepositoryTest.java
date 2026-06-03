package ua.nure.ice.bookcatalog.laboratorna3.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.laboratorna3.model.order.BookOrder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    @Test
    void save_ShouldAddOrderAndReturnIt() {
        BookOrder order = new BookOrder.Builder("ORD-001", "Alice").build();
        BookOrder savedOrder = repository.save(order);

        assertNotNull(savedOrder);
        assertEquals("ORD-001", savedOrder.getOrderId());
        
        Optional<BookOrder> foundOrder = repository.findById("ORD-001");
        assertTrue(foundOrder.isPresent());
        assertEquals(order, foundOrder.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenOrderDoesNotExist() {
        Optional<BookOrder> foundOrder = repository.findById("UNKNOWN-ID");
        assertFalse(foundOrder.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllSavedOrders() {
        BookOrder order1 = new BookOrder.Builder("ORD-001", "Alice").build();
        BookOrder order2 = new BookOrder.Builder("ORD-002", "Bob").build();

        repository.save(order1);
        repository.save(order2);

        List<BookOrder> orders = repository.findAll();
        assertEquals(2, orders.size());
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
    }

    @Test
    void deleteById_ShouldRemoveOrderAndReturnTrue_WhenOrderExists() {
        BookOrder order = new BookOrder.Builder("ORD-001", "Alice").build();
        repository.save(order);

        boolean deleted = repository.deleteById("ORD-001");
        assertTrue(deleted);

        Optional<BookOrder> foundOrder = repository.findById("ORD-001");
        assertFalse(foundOrder.isPresent());
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenOrderDoesNotExist() {
        boolean deleted = repository.deleteById("UNKNOWN-ID");
        assertFalse(deleted);
    }
}
