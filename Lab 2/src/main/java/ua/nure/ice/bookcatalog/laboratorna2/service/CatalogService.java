package ua.nure.ice.bookcatalog.laboratorna2.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import ua.nure.ice.bookcatalog.laboratorna2.exception.BookNotFoundException;
import ua.nure.ice.bookcatalog.laboratorna2.exception.DuplicateBookException;
import ua.nure.ice.bookcatalog.laboratorna2.exception.InvalidBookDataException;
import ua.nure.ice.bookcatalog.laboratorna2.model.Book;
import ua.nure.ice.bookcatalog.laboratorna2.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna2.repository.BookRepository;

import ua.nure.ice.bookcatalog.laboratorna2.repository.OrderRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogService {
  private final BookRepository bookRepository;
  private final OrderRepository orderRepository;

  public CatalogService(BookRepository bookRepository, OrderRepository orderRepository) {
    this.bookRepository = Objects.requireNonNull(bookRepository, "Repository must not be null.");
    this.orderRepository = Objects.requireNonNull(orderRepository, "Order Repository must not be null.");
  }

  @Transactional
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

  @Transactional
  public Book updateBook(long id, String title, String author, int publicationYear, BookGenre genre) {
    validateBookInput(title, author, publicationYear, genre);

    Book existingBook = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found."));

    bookRepository.findDuplicate(title, author, publicationYear)
        .filter(b -> b.getId() != id)
        .ifPresent(duplicate -> {
          throw new DuplicateBookException("Book already exists in catalog.");
        });

    existingBook.setTitle(title);
    existingBook.setAuthor(author);
    existingBook.setPublicationYear(publicationYear);
    existingBook.setGenre(genre);

    return bookRepository.save(existingBook);
  }

  @Transactional
  public void removeBook(long id) {
    if (id <= 0) {
      throw new InvalidBookDataException("Book id must be greater than zero.");
    }

    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException("Book with id " + id + " not found.");
    }

    if (orderRepository.existsByBooks_Id(id)) {
      throw new IllegalStateException("Book is part of an order and cannot be deleted");
    }

    bookRepository.deleteById(id);
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
