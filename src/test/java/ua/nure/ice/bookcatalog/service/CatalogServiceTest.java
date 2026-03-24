package ua.nure.ice.bookcatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.exception.BookNotFoundException;
import ua.nure.ice.bookcatalog.exception.DuplicateBookException;
import ua.nure.ice.bookcatalog.exception.InvalidBookDataException;
import ua.nure.ice.bookcatalog.model.Book;
import ua.nure.ice.bookcatalog.model.BookGenre;
import ua.nure.ice.bookcatalog.repository.InMemoryBookRepository;

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
  }
}
