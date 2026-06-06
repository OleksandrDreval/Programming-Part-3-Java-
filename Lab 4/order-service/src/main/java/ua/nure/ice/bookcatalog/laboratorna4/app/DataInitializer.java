package ua.nure.ice.bookcatalog.laboratorna4.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna4.repository.BookRepository;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }
            bookRepository.saveAll(List.of(
                    new Book("1984", "George Orwell", 1949, BookGenre.FICTION, new BigDecimal("350.00")),
                    new Book("A Brief History of Time", "Stephen Hawking", 1988, BookGenre.SCIENCE_FICTION, new BigDecimal("520.00")),
                    new Book("Sapiens", "Yuval Noah Harari", 2011, BookGenre.HISTORY, new BigDecimal("480.00")),
                    new Book("The Hobbit", "J.R.R. Tolkien", 1937, BookGenre.FANTASY, new BigDecimal("400.00"))
            ));
        };
    }
}
