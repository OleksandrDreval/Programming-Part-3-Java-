package ua.nure.ice.bookcatalog.laboratorna3.model;

import java.time.Year;
import java.util.Objects;

public final class Book {
  private static final int MIN_PUBLICATION_YEAR = 1450;
  private static final int MAX_PUBLICATION_YEAR_OFFSET = 1;

  private long id;
  private String title;
  private String author;
  private int publicationYear;
  private BookGenre genre;

  public Book() {}

  public Book(long id, String title, String author, int publicationYear, BookGenre genre) {
    this.id = validateId(id);
    this.title = validateText(title, "Title");
    this.author = validateText(author, "Author");
    this.publicationYear = validatePublicationYear(publicationYear);
    this.genre = Objects.requireNonNull(genre, "Genre must not be null.");
  }

  public Book(String title, String author, int publicationYear, BookGenre genre) {
    this(0L, title, author, publicationYear, genre);
  }

  public Book withId(long newId) {
    return new Book(newId, title, author, publicationYear, genre);
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public int getPublicationYear() {
    return publicationYear;
  }

  public BookGenre getGenre() {
    return genre;
  }

  @Override
  public String toString() {
    return "Book{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", author='" + author + '\'' +
        ", publicationYear=" + publicationYear +
        ", genre=" + genre +
        '}';
  }

  private static long validateId(long id) {
    if (id < 0) {
      throw new IllegalArgumentException("Id must be positive or zero.");
    }
    return id;
  }

  private static String validateText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be empty.");
    }
    return value.trim();
  }

  private static int validatePublicationYear(int publicationYear) {
    int maxPublicationYear = Year.now().getValue() + MAX_PUBLICATION_YEAR_OFFSET;
    if (publicationYear < MIN_PUBLICATION_YEAR || publicationYear > maxPublicationYear) {
      throw new IllegalArgumentException("Publication year must be between "
          + MIN_PUBLICATION_YEAR + " and " + maxPublicationYear + '.');
    }
    return publicationYear;
  }
}
