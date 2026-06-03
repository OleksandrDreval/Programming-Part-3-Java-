package ua.nure.ice.bookcatalog.laboratorna3.repository;

import java.util.List;
import java.util.Optional;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  List<Book> findByGenre(BookGenre genre);

  @Query("SELECT b FROM Book b WHERE b.title = :title AND b.author = :author AND b.publicationYear = :publicationYear")
  Optional<Book> findDuplicate(@Param("title") String title, @Param("author") String author, @Param("publicationYear") int publicationYear);
}
