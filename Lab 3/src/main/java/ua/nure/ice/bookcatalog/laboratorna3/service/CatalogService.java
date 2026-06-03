package ua.nure.ice.bookcatalog.laboratorna3.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import ua.nure.ice.bookcatalog.laboratorna3.exception.BookNotFoundException;
import ua.nure.ice.bookcatalog.laboratorna3.exception.DuplicateBookException;
import ua.nure.ice.bookcatalog.laboratorna3.exception.InvalidBookDataException;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.repository.BookRepository;

@Service
public class CatalogService {
  private final BookRepository bookRepository;

  public CatalogService(BookRepository bookRepository) {
    this.bookRepository = Objects.requireNonNull(bookRepository, "Repository must not be null.");
  }

  public Book addBook(String title, String author, int publicationYear, BookGenre genre) {
    validateBookInput(title, author, publicationYear, genre);

    bookRepository.findDuplicate(title, author, publicationYear)
        .ifPresent(duplicate -> {
          throw new DuplicateBookException("Book already exists in catalog.");
        });

    try {
      Book newBook = new Book(title, author, publicationYear, genre);
      return bookRepository.save(newBook);
    } catch (IllegalArgumentException exception) {
      throw new InvalidBookDataException(exception.getMessage());
    }
  }

  public void removeBook(long id) {
    if (id <= 0) {
      throw new InvalidBookDataException("Book id must be greater than zero.");
    }

    boolean removed = bookRepository.deleteById(id);
    if (!removed) {
      throw new BookNotFoundException("Book with id " + id + " not found.");
    }
  }

  public List<Book> findAllBooks() {
    return bookRepository.findAll();
  }

  public List<Book> findBooksByGenre(BookGenre genre) {
    if (genre == null) {
      throw new InvalidBookDataException("Genre must not be null.");
    }
    return bookRepository.findByGenre(genre);
  }

  private void validateBookInput(String title, String author, int publicationYear, BookGenre genre) {
    if (title == null || title.isBlank()) {
      throw new InvalidBookDataException("Title must not be empty.");
    }
    if (author == null || author.isBlank()) {
      throw new InvalidBookDataException("Author must not be empty.");
    }
    if (genre == null) {
      throw new InvalidBookDataException("Genre must not be null.");
    }
    if (publicationYear <= 0) {
      throw new InvalidBookDataException("Publication year must be positive.");
    }
  }
}
