package ua.nure.ice.bookcatalog.laboratorna1.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ua.nure.ice.bookcatalog.laboratorna1.model.Book;
import ua.nure.ice.bookcatalog.laboratorna1.model.BookGenre;

class BookOrderTest {

    @Test
    void builderShouldCreateValidOrder() {
        Book book1 = new Book(1, "The Hobbit", "J.R.R. Tolkien", 1937, BookGenre.FANTASY);
        Book book2 = new Book(2, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);

        DeliveryAddress address = new DeliveryAddress.Builder("Ukraine", "Kyiv", "Khreshchatyk", "1").build();

        BookOrder order = new BookOrder.Builder("ORD-001", "Ivan Petrenko")
                .addBook(book1)
                .addBook(book2)
                .deliveryAddress(address)
                .isUrgent(true)
                .paymentMethod("Credit Card")
                .orderComment("Please call before delivery")
                .build();

        assertEquals("ORD-001", order.getOrderId());
        assertEquals("Ivan Petrenko", order.getCustomerName());
        assertEquals(2, order.getBooks().size());
        assertEquals("The Hobbit", order.getBooks().get(0).getTitle());
        assertEquals("1984", order.getBooks().get(1).getTitle());
        assertNotNull(order.getDeliveryAddress());
        assertTrue(order.isUrgent());
        assertEquals("Credit Card", order.getPaymentMethod());
        assertEquals("Please call before delivery", order.getOrderComment());
        
        String toString = order.toString();
        assertTrue(toString.contains("ORD-001"));
        assertTrue(toString.contains("Ivan Petrenko"));
        assertTrue(toString.contains("Credit Card"));
        assertTrue(toString.contains("Please call before delivery"));
        assertTrue(toString.contains("The Hobbit"));
        assertTrue(toString.contains("1984"));
    }

    @Test
    void builderShouldCreateOrderWithOnlyRequiredFields() {
        BookOrder order = new BookOrder.Builder("ORD-002", "Olena").build();

        assertEquals("ORD-002", order.getOrderId());
        assertEquals("Olena", order.getCustomerName());
        assertTrue(order.getBooks().isEmpty());
        assertNull(order.getDeliveryAddress());
        assertFalse(order.isUrgent());
        assertNull(order.getPaymentMethod());
        assertNull(order.getOrderComment());
        
        String toString = order.toString();
        assertTrue(toString.contains("ORD-002"));
        assertTrue(toString.contains("Olena"));
        assertTrue(toString.contains("Not specified"));
        assertTrue(toString.contains("No books in order"));
    }

    @Test
    void builderShouldThrowWhenCustomerNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new BookOrder.Builder("ORD-001", ""));
        assertThrows(IllegalArgumentException.class, () -> new BookOrder.Builder("ORD-001", "   "));
        assertThrows(IllegalArgumentException.class, () -> new BookOrder.Builder("ORD-001", null));
    }
    
    @Test
    void builderShouldHandleNullAdditions() {
        BookOrder order = new BookOrder.Builder("ORD-003", "Test")
                .addBook(null)
                .books(null)
                .build();
                
        assertTrue(order.getBooks().isEmpty());
    }
    
    @Test
    void builderShouldHandleBooksList() {
        Book book1 = new Book(1, "The Hobbit", "J.R.R. Tolkien", 1937, BookGenre.FANTASY);
        BookOrder order = new BookOrder.Builder("ORD-004", "Test")
                .books(List.of(book1))
                .build();
                
        assertEquals(1, order.getBooks().size());
        assertEquals("The Hobbit", order.getBooks().get(0).getTitle());
    }
    
    @Test
    void builderShouldHandleEmptyOptionalFields() {
        BookOrder order = new BookOrder.Builder("ORD-005", "Test")
                .orderComment("")
                .paymentMethod("")
                .build();
                
        String toString = order.toString();
        assertFalse(toString.contains("Comment:"));
        assertTrue(toString.contains("Payment Method: "));
    }
}
