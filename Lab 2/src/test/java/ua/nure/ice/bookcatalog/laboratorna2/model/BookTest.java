package ua.nure.ice.bookcatalog.laboratorna2.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BookTest {

  @Test
  void withIdShouldReturnCopyWithNewId() {
    Book book = new Book("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);

    Book persistedBook = book.withId(10L);

    assertEquals(10L, persistedBook.getId());
    assertEquals("Dune", persistedBook.getTitle());
    assertEquals("Frank Herbert", persistedBook.getAuthor());
    assertEquals(1965, persistedBook.getPublicationYear());
    assertEquals(BookGenre.SCIENCE_FICTION, persistedBook.getGenre());
  }

  @Test
  void constructorShouldThrowWhenIdIsNegative() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book(-1L, "Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void constructorShouldThrowWhenTitleIsBlank() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book(" ", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void constructorShouldThrowWhenTitleIsNull() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book(null, "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void constructorShouldThrowWhenAuthorIsBlank() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book("Dune", " ", 1965, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void constructorShouldThrowWhenAuthorIsNull() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book("Dune", null, 1965, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void constructorShouldThrowWhenGenreIsNull() {
    assertThrows(NullPointerException.class,
        () -> new Book("Dune", "Frank Herbert", 1965, null));
  }

  @Test
  void constructorShouldThrowWhenYearTooSmall() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book("Dune", "Frank Herbert", 1400, BookGenre.SCIENCE_FICTION));
  }

  @Test
  void constructorShouldThrowWhenYearTooLarge() {
    assertThrows(IllegalArgumentException.class,
        () -> new Book("Dune", "Frank Herbert", 3000, BookGenre.SCIENCE_FICTION));
  }
  
  @Test
  void toStringShouldContainAllFields() {
    Book book = new Book(1L, "Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);
    String str = book.toString();
    org.junit.jupiter.api.Assertions.assertTrue(str.contains("Dune"));
    org.junit.jupiter.api.Assertions.assertTrue(str.contains("Frank Herbert"));
    org.junit.jupiter.api.Assertions.assertTrue(str.contains("1965"));
    org.junit.jupiter.api.Assertions.assertTrue(str.contains("SCIENCE_FICTION"));
  }
}
