package ua.nure.ice.bookcatalog.practical4.model.order;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ua.nure.ice.bookcatalog.practical4.model.Book;
import ua.nure.ice.bookcatalog.practical4.model.BookGenre;

class BookOrderTest {

    @Test
    void testEmptyConstructor() {
        BookOrder order = new BookOrder();
        assertNotNull(order);
    }

    @Test
    void testToString_WithAllFields() {
        Book book = new Book(1L, "Test Title", "Author", 2023, BookGenre.FICTION);
        DeliveryAddress address = new DeliveryAddress.Builder("Country", "City", "Street", "1")
                .postalCode("00000")
                .build();
        
        BookOrder order = new BookOrder.Builder(1L, "John Doe")
                .addBook(book)
                .deliveryAddress(address)
                .orderComment("Please deliver fast")
                .isUrgent(true)
                .paymentMethod("Credit Card")
                .status(OrderStatus.APPROVED)
                .build();
                
        String toString = order.toString();
        assertTrue(toString.contains("Order #1"));
        assertTrue(toString.contains("Customer: John Doe"));
        assertTrue(toString.contains("Urgent: Yes"));
        assertTrue(toString.contains("Payment Method: Credit Card"));
        assertTrue(toString.contains("Comment: Please deliver fast"));
        assertTrue(toString.contains("Country"));
        assertTrue(toString.contains("Test Title by Author"));
        
        order.setStatus(OrderStatus.COMPLETED);
        assertTrue(order.getStatus() == OrderStatus.COMPLETED);
    }

    @Test
    void testToString_WithMissingFields() {
        BookOrder order = new BookOrder.Builder("Jane Doe")
                .books(List.of())
                .isUrgent(false)
                .build();
                
        String toString = order.toString();
        assertTrue(toString.contains("Customer: Jane Doe"));
        assertTrue(toString.contains("Urgent: No"));
        assertTrue(toString.contains("Payment Method: Not specified"));
        assertTrue(toString.contains("Not specified"));
        assertTrue(toString.contains("No books in order"));
    }

    @Test
    void testBuilderNulls() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> new BookOrder.Builder(null));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> new BookOrder.Builder("   "));

        BookOrder order = new BookOrder.Builder("Jane Doe")
                .addBook(null)
                .books(null)
                .orderComment("")
                .build();
        assertTrue(order.getBooks().isEmpty());
        assertTrue(order.toString().contains("Jane Doe"));
    }
}
