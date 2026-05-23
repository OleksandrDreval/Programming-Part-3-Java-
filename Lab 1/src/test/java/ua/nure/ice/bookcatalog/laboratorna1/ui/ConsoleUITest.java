package ua.nure.ice.bookcatalog.laboratorna1.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.laboratorna1.repository.InMemoryBookRepository;
import ua.nure.ice.bookcatalog.laboratorna1.service.CatalogService;

class ConsoleUITest {
  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputStream;

  @BeforeEach
  void setUp() {
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  @Test
  void startShouldHandleUnknownCommandAndExit() {
    System.setIn(new ByteArrayInputStream("9\n0\n".getBytes(StandardCharsets.UTF_8)));
    ConsoleUI consoleUI = new ConsoleUI(new CatalogService(new InMemoryBookRepository()));

    consoleUI.start();

    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Unknown command. Try again."));
    assertTrue(output.contains("Goodbye."));
  }

  @Test
  void startShouldHandleRuntimeErrorAndContinue() {
    System.setIn(new ByteArrayInputStream("2\nabc\n0\n".getBytes(StandardCharsets.UTF_8)));
    ConsoleUI consoleUI = new ConsoleUI(new CatalogService(new InMemoryBookRepository()));

    consoleUI.start();

    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Error: You must enter a valid long number."));
    assertTrue(output.contains("Goodbye."));
  }

  @Test
  void startShouldPrintActionCompletedForNonExitCommand() {
    System.setIn(new ByteArrayInputStream("3\n0\n".getBytes(StandardCharsets.UTF_8)));
    ConsoleUI consoleUI = new ConsoleUI(new CatalogService(new InMemoryBookRepository()));

    consoleUI.start();

    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Catalog is empty."));
    assertTrue(output.contains("Action completed."));
  }

  @Test
  void startShouldNotPrintActionCompletedAfterExitCommand() {
    System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));
    ConsoleUI consoleUI = new ConsoleUI(new CatalogService(new InMemoryBookRepository()));

    consoleUI.start();

    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertFalse(output.contains("Action completed."));
    assertTrue(output.contains("Application is shutting down."));
  }
}
