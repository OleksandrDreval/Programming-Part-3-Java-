package ua.nure.ice.bookcatalog.practice3.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import ua.nure.ice.bookcatalog.practice3.model.Book;
import ua.nure.ice.bookcatalog.practice3.model.BookGenre;

public class InMemoryBookRepository implements BookRepository {
  private final Map<Long, Book> booksById;
  private long nextId;

  public InMemoryBookRepository() {
    this.booksById = new LinkedHashMap<>();
    this.nextId = 1L;
  }

  @Override
  public Book save(Book book) {
    Objects.requireNonNull(book, "Book must not be null.");

    Book bookToStore = book;
    if (book.getId() == 0) {
      bookToStore = book.withId(nextId++);
    }

    booksById.put(bookToStore.getId(), bookToStore);
    return bookToStore;
  }

  @Override
  public boolean deleteById(long id) {
    return booksById.remove(id) != null;
  }

  @Override
  public List<Book> findAll() {
    return new ArrayList<>(booksById.values());
  }

  @Override
  public Optional<Book> findById(long id) {
    return Optional.ofNullable(booksById.get(id));
  }

  @Override
  public List<Book> findByGenre(BookGenre genre) {
    Objects.requireNonNull(genre, "Genre must not be null.");

    List<Book> result = new ArrayList<>();
    for (Book book : booksById.values()) {
      if (book.getGenre() == genre) {
        result.add(book);
      }
    }
    return result;
  }

  @Override
  public Optional<Book> findDuplicate(String title, String author, int publicationYear) {
    String normalizedTitle = normalize(title);
    String normalizedAuthor = normalize(author);

    for (Book book : booksById.values()) {
      if (normalize(book.getTitle()).equals(normalizedTitle)
          && normalize(book.getAuthor()).equals(normalizedAuthor)
          && book.getPublicationYear() == publicationYear) {
        return Optional.of(book);
      }
    }
    return Optional.empty();
  }

  private String normalize(String value) {
    return value.trim().toLowerCase(Locale.ROOT);
  }
}
