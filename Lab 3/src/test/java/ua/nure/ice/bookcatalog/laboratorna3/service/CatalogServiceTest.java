package ua.nure.ice.bookcatalog.laboratorna3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.nure.ice.bookcatalog.laboratorna3.exception.BookNotFoundException;
import ua.nure.ice.bookcatalog.laboratorna3.exception.DuplicateBookException;
import ua.nure.ice.bookcatalog.laboratorna3.exception.InvalidBookDataException;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.repository.BookRepository;
import ua.nure.ice.bookcatalog.laboratorna3.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CatalogService catalogService;

    private Book validBook;

    @BeforeEach
    void setUp() {
        validBook = new Book(1L, "Test Title", "Test Author", 2023, BookGenre.FICTION);
    }

    @Test
    void addBook_valid_success() {
        when(bookRepository.findDuplicate("Test Title", "Test Author", 2023)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        Book saved = catalogService.addBook("Test Title", "Test Author", 2023, BookGenre.FICTION);
        assertNotNull(saved);
        assertEquals("Test Title", saved.getTitle());
    }

    @Test
    void addBook_duplicate_throwsException() {
        when(bookRepository.findDuplicate("Test Title", "Test Author", 2023)).thenReturn(Optional.of(validBook));
        assertThrows(DuplicateBookException.class, () -> catalogService.addBook("Test Title", "Test Author", 2023, BookGenre.FICTION));
    }

    @Test
    void addBook_invalidInput_throwsException() {
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook(null, "Author", 2023, BookGenre.FICTION));
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook("", "Author", 2023, BookGenre.FICTION));
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook("Title", null, 2023, BookGenre.FICTION));
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook("Title", "", 2023, BookGenre.FICTION));
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook("Title", "Author", 2023, null));
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook("Title", "Author", 0, BookGenre.FICTION));
    }
    
    @Test
    void addBook_illegalArgumentFromBook_throwsException() {
        when(bookRepository.findDuplicate("Title", "Author", 1000)).thenReturn(Optional.empty());
        assertThrows(InvalidBookDataException.class, () -> catalogService.addBook("Title", "Author", 1000, BookGenre.FICTION));
    }

    @Test
    void updateBook_success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(validBook));
        when(bookRepository.findDuplicate("New Title", "New Author", 2024)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        Book updated = catalogService.updateBook(1L, "New Title", "New Author", 2024, BookGenre.SCIENCE_FICTION);
        assertEquals("New Title", updated.getTitle());
        assertEquals(BookGenre.SCIENCE_FICTION, updated.getGenre());
    }

    @Test
    void updateBook_notFound_throwsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> catalogService.updateBook(1L, "Title", "Author", 2024, BookGenre.FICTION));
    }

    @Test
    void updateBook_duplicate_throwsException() {
        Book otherBook = new Book(2L, "New Title", "New Author", 2024, BookGenre.FICTION);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(validBook));
        when(bookRepository.findDuplicate("New Title", "New Author", 2024)).thenReturn(Optional.of(otherBook));

        assertThrows(DuplicateBookException.class, () -> catalogService.updateBook(1L, "New Title", "New Author", 2024, BookGenre.FICTION));
    }

    @Test
    void updateBook_duplicateSameId_success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(validBook));
        when(bookRepository.findDuplicate("New Title", "New Author", 2024)).thenReturn(Optional.of(validBook));
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        Book updated = catalogService.updateBook(1L, "New Title", "New Author", 2024, BookGenre.SCIENCE_FICTION);
        assertEquals("New Title", updated.getTitle());
    }

    @Test
    void removeBook_success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.existsByBooks_Id(1L)).thenReturn(false);

        assertDoesNotThrow(() -> catalogService.removeBook(1L));
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void removeBook_invalidId_throwsException() {
        assertThrows(InvalidBookDataException.class, () -> catalogService.removeBook(0L));
    }

    @Test
    void removeBook_notFound_throwsException() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        assertThrows(BookNotFoundException.class, () -> catalogService.removeBook(1L));
    }

    @Test
    void removeBook_inOrder_throwsException() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.existsByBooks_Id(1L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> catalogService.removeBook(1L));
    }

    @Test
    void findBooksByGenre_success() {
        when(bookRepository.findByGenre(BookGenre.FICTION)).thenReturn(List.of(validBook));
        List<Book> books = catalogService.findBooksByGenre(BookGenre.FICTION);
        assertEquals(1, books.size());
    }

    @Test
    void findBooksByGenre_null_throwsException() {
        assertThrows(InvalidBookDataException.class, () -> catalogService.findBooksByGenre(null));
    }

    @Test
    void findAllBooks_success() {
        when(bookRepository.findAll()).thenReturn(List.of(validBook));
        assertEquals(1, catalogService.findAllBooks().size());
    }
}
