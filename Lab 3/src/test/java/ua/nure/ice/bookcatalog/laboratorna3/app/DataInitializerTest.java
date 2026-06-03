package ua.nure.ice.bookcatalog.laboratorna3.app;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import ua.nure.ice.bookcatalog.laboratorna3.repository.BookRepository;

class DataInitializerTest {

    @Test
    void testSeedBooksWhenEmpty() throws Exception {
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.count()).thenReturn(0L);

        DataInitializer dataInitializer = new DataInitializer();
        CommandLineRunner runner = dataInitializer.seedBooks(bookRepository);
        runner.run();

        verify(bookRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void testSeedBooksWhenNotEmpty() throws Exception {
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.count()).thenReturn(5L);

        DataInitializer dataInitializer = new DataInitializer();
        CommandLineRunner runner = dataInitializer.seedBooks(bookRepository);
        runner.run();

        verify(bookRepository, never()).saveAll(any());
    }
}
