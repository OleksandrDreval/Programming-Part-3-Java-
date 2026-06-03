package ua.nure.ice.bookcatalog.laboratorna3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.laboratorna3.exception.BookNotFoundException;
import ua.nure.ice.bookcatalog.laboratorna3.exception.DuplicateBookException;
import ua.nure.ice.bookcatalog.laboratorna3.exception.InvalidBookDataException;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.repository.BookRepository;
import ua.nure.ice.bookcatalog.laboratorna3.repository.InMemoryBookRepository;

class CatalogServiceTest {
  private CatalogService catalogService;

  @BeforeEach
  void setUp() {
    catalogService = new CatalogService(new InMemoryBookRepository());
  }

  @Test
  void addBookShouldAssignIdAndStoreBook() {
    Book book = catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);

    assertEquals(1L, book.getId());
    assertEquals(1, catalogService.findAllBooks().size());
  }

  @Test
  void addBookShouldThrowOnDuplicate() {
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);

    assertThrows(DuplicateBookException.class,
        () -> catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void removeBookShouldThrowWhenIdDoesNotExist() {
    assertThrows(BookNotFoundException.class, () -> catalogService.removeBook(101));
  }

  @Test
  void findBooksByGenreShouldReturnOnlyMatchingBooks() {
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);
    catalogService.addBook("1984", "George Orwell", 1949, BookGenre.FICTION);

    List<Book> scienceFictionBooks = catalogService.findBooksByGenre(BookGenre.SCIENCE_FICTION);

    assertEquals(1, scienceFictionBooks.size());
    assertEquals("Dune", scienceFictionBooks.getFirst().getTitle());
  }

  @Test
  void addBookShouldThrowOnInvalidData() {
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook("", "Author", 2020, BookGenre.FICTION));
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook(null, "Author", 2020, BookGenre.FICTION));
  }

  @Test
  void constructorShouldThrowWhenRepositoryIsNull() {
    assertThrows(NullPointerException.class, () -> new CatalogService((BookRepository) null));
  }

  @Test
  void removeBookShouldThrowWhenIdIsZero() {
    assertThrows(InvalidBookDataException.class, () -> catalogService.removeBook(0));
  }

  @Test
  void findBooksByGenreShouldThrowWhenGenreIsNull() {
    assertThrows(InvalidBookDataException.class, () -> catalogService.findBooksByGenre(null));
  }

  @Test
  void addBookShouldThrowWhenAuthorIsBlankOrNull() {
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook("Dune", "  ", 1965, BookGenre.FICTION));
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook("Dune", null, 1965, BookGenre.FICTION));
  }

  @Test
  void addBookShouldThrowWhenGenreIsNull() {
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook("Dune", "Frank Herbert", 1965, null));
  }

  @Test
  void addBookShouldThrowWhenYearIsNonPositive() {
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook("Dune", "Frank Herbert", 0, BookGenre.FICTION));
  }

  @Test
  void addBookShouldWrapModelValidationException() {
    assertThrows(InvalidBookDataException.class,
        () -> catalogService.addBook("Dune", "Frank Herbert", 1400, BookGenre.FICTION));
  }
}
