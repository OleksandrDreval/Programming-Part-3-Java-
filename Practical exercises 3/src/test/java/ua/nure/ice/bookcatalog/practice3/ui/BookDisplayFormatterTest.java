package ua.nure.ice.bookcatalog.practice3.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.practice3.model.Book;
import ua.nure.ice.bookcatalog.practice3.model.BookGenre;

class BookDisplayFormatterTest {

  @Test
  void formatShouldContainAllBookFields() {
    Book book = new Book(7L, "Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);

    String formattedBook = BookDisplayFormatter.format(book);

    assertTrue(formattedBook.contains("ID: 7"));
    assertTrue(formattedBook.contains("Title: Dune"));
    assertTrue(formattedBook.contains("Author: Frank Herbert"));
    assertTrue(formattedBook.contains("Year: 1965"));
    assertTrue(formattedBook.contains("Genre: SCIENCE_FICTION"));
  }
}
