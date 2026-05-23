package ua.nure.ice.bookcatalog.laboratorna1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.laboratorna1.model.Book;
import ua.nure.ice.bookcatalog.laboratorna1.model.BookGenre;

class InMemoryBookRepositoryTest {
  private InMemoryBookRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryBookRepository();
  }

  @Test
  void saveShouldAssignIncrementalIdsForNewBooks() {
    Book firstBook = repository.save(new Book("Book One", "Author A", 2001, BookGenre.OTHER));
    Book secondBook = repository.save(new Book("Book Two", "Author B", 2002, BookGenre.OTHER));

    assertEquals(1L, firstBook.getId());
    assertEquals(2L, secondBook.getId());
  }

  @Test
  void deleteByIdShouldReturnTrueWhenBookExists() {
    Book savedBook = repository.save(new Book("Book One", "Author A", 2001, BookGenre.OTHER));

    boolean removed = repository.deleteById(savedBook.getId());

    assertTrue(removed);
    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void deleteByIdShouldReturnFalseWhenBookDoesNotExist() {
    boolean removed = repository.deleteById(100L);

    assertFalse(removed);
  }

  @Test
  void findDuplicateShouldMatchByTitleAuthorAndYearIgnoringCase() {
    repository.save(new Book("The Hobbit", "J.R.R. Tolkien", 1937, BookGenre.FANTASY));

    boolean duplicateFound = repository.findDuplicate("the hobbit", "j.r.r. tolkien", 1937).isPresent();
    assertTrue(duplicateFound);
    
    boolean duplicateNotFound = repository.findDuplicate("the hobbit", "j.r.r. tolkien", 1938).isPresent();
    assertFalse(duplicateNotFound);
  }

  @Test
  void findByGenreShouldReturnOnlyMatching() {
    repository.save(new Book("The Hobbit", "J.R.R. Tolkien", 1937, BookGenre.FANTASY));
    repository.save(new Book("1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION));
    
    assertEquals(1, repository.findByGenre(BookGenre.FANTASY).size());
    assertEquals(0, repository.findByGenre(BookGenre.HISTORY).size());
  }

  @Test
  void saveWithExistingIdShouldKeepId() {
    Book book = new Book(99L, "Test", "Test", 2000, BookGenre.OTHER);
    Book saved = repository.save(book);
    assertEquals(99L, saved.getId());
  }
}
