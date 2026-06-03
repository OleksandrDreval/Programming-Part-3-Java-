package ua.nure.ice.bookcatalog.laboratorna3.ui.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.repository.InMemoryBookRepository;
import ua.nure.ice.bookcatalog.laboratorna3.service.CatalogService;
import ua.nure.ice.bookcatalog.laboratorna3.ui.ConsoleInputHelper;

class CommandTest {
  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputStream;

  @BeforeEach
  void setUp() {
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void addBookCommandShouldAddBookAndPrintSuccessMessage() {
    CatalogService catalogService = new CatalogService(new InMemoryBookRepository());
    ConsoleInputHelper inputHelper = createInputHelper("Dune\nFrank Herbert\n1965\n1\n");
    AddBookCommand command = new AddBookCommand(catalogService, inputHelper);

    command.execute();

    assertEquals(1, catalogService.findAllBooks().size());
    assertTrue(output().contains("Book was added successfully."));
  }

  @Test
  void removeBookCommandShouldRemoveBookAndPrintSuccessMessage() {
    CatalogService catalogService = new CatalogService(new InMemoryBookRepository());
    long bookId = catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION).getId();
    ConsoleInputHelper inputHelper = createInputHelper(bookId + "\n");
    RemoveBookCommand command = new RemoveBookCommand(catalogService, inputHelper);

    command.execute();

    assertTrue(catalogService.findAllBooks().isEmpty());
    assertTrue(output().contains("Book was removed successfully."));
  }

  @Test
  void showAllBooksCommandShouldPrintEmptyCatalogMessage() {
    CatalogService catalogService = new CatalogService(new InMemoryBookRepository());
    ShowAllBooksCommand command = new ShowAllBooksCommand(catalogService);

    command.execute();

    assertTrue(output().contains("Catalog is empty."));
  }

  @Test
  void showAllBooksCommandShouldPrintBookListWhenCatalogNotEmpty() {
    CatalogService catalogService = new CatalogService(new InMemoryBookRepository());
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.FICTION);
    ShowAllBooksCommand command = new ShowAllBooksCommand(catalogService);

    command.execute();

    assertTrue(output().contains("Total books: 1"));
    assertTrue(output().contains("Title: Dune"));
  }

  @Test
  void showBooksByGenreCommandShouldPrintFoundBooks() {
    CatalogService catalogService = new CatalogService(new InMemoryBookRepository());
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.FICTION);
    ConsoleInputHelper inputHelper = createInputHelper("1\n");
    ShowBooksByGenreCommand command = new ShowBooksByGenreCommand(catalogService, inputHelper);

    command.execute();

    assertTrue(output().contains("Found books: 1"));
    assertTrue(output().contains("Title: Dune"));
  }

  @Test
  void showBooksByGenreCommandShouldPrintNoBooksMessageWhenNotFound() {
    CatalogService catalogService = new CatalogService(new InMemoryBookRepository());
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.FICTION);
    ConsoleInputHelper inputHelper = createInputHelper("3\n");
    ShowBooksByGenreCommand command = new ShowBooksByGenreCommand(catalogService, inputHelper);

    command.execute();

    assertTrue(output().contains("Selected genre: FANTASY"));
    assertTrue(output().contains("No books found for selected genre."));
  }

  @Test
  void exitCommandShouldInvokeExitCallback() {
    boolean[] exited = new boolean[]{false};
    ExitCommand command = new ExitCommand(() -> exited[0] = true);

    command.execute();

    assertTrue(exited[0]);
    assertTrue(output().contains("Application is shutting down."));
  }

  private ConsoleInputHelper createInputHelper(String input) {
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    return new ConsoleInputHelper(new Scanner(inputStream));
  }

  private String output() {
    return outputStream.toString(StandardCharsets.UTF_8);
  }
}
