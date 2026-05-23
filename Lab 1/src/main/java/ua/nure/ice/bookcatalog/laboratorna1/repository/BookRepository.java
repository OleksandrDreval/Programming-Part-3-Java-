package ua.nure.ice.bookcatalog.laboratorna1.repository;

import java.util.List;
import java.util.Optional;
import ua.nure.ice.bookcatalog.laboratorna1.model.Book;
import ua.nure.ice.bookcatalog.laboratorna1.model.BookGenre;

public interface BookRepository {
  Book save(Book book);

  boolean deleteById(long id);

  List<Book> findAll();

  Optional<Book> findById(long id);

  List<Book> findByGenre(BookGenre genre);

  Optional<Book> findDuplicate(String title, String author, int publicationYear);
}
