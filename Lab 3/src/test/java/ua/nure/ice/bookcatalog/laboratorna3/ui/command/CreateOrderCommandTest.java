package ua.nure.ice.bookcatalog.laboratorna3.ui.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

class CreateOrderCommandTest {
  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputStream;
  private CatalogService catalogService;

  @BeforeEach
  void setUp() {
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    catalogService = new CatalogService(new InMemoryBookRepository());
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void executeShouldCreateOrderAndApplyDecorators() {
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);
    
    // Simulate user input for successful order with all optional fields and all decorators
    String input = "ORD-001\n" +      // Order ID
                   "Ivan\n" +         // Customer Name
                   "1\n" +            // Add book 1
                   "99\n" +           // Add non-existent book
                   "0\n" +            // Finish books
                   "y\n" +            // Yes to address
                   "UA\n" +           // Country
                   "Kyiv\n" +         // City
                   "Kreschatyk\n" +   // Street
                   "1\n" +            // Building
                   "1a\n" +           // Apartment
                   "01001\n" +        // Postal
                   "call me\n" +      // Details
                   "y\n" +            // Urgent
                   "card\n" +         // Payment
                   "ASAP\n" +         // Comment
                   "y\n" +            // Send notification
                   "1\n" +            // Log
                   "2\n" +            // Time
                   "3\n" +            // Encrypt
                   "4\n" +            // Sign
                   "9\n" +            // Invalid decorator
                   "0\n";             // Finish decorators

    ConsoleInputHelper inputHelper = createInputHelper(input);
    CreateOrderCommand command = new CreateOrderCommand(catalogService, inputHelper);
    
    assertDoesNotThrow(command::execute);
    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Order Successfully Created"));
    assertTrue(output.contains("ORD-001"));
    assertTrue(output.contains("Ivan"));
    assertTrue(output.contains("Dune"));
    assertTrue(output.contains("Kreschatyk"));
    assertTrue(output.contains("not found"));
    assertTrue(output.contains("Invalid choice"));
    assertTrue(output.contains("Final message"));
    assertTrue(command.getTitle().contains("Create new order"));
  }

  @Test
  void executeShouldHandleEmptyCatalog() {
    String input = "ORD-002\nOleh\n";
    ConsoleInputHelper inputHelper = createInputHelper(input);
    CreateOrderCommand command = new CreateOrderCommand(catalogService, inputHelper);
    
    assertDoesNotThrow(command::execute);
    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Catalog is empty. Cannot create an order."));
  }

  @Test
  void executeShouldHandleNoAddressAndNoNotificationAndBlankOptionals() {
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);
    
    String input = "ORD-003\n" +      // Order ID
                   "Oleh\n" +         // Customer Name
                   "0\n" +            // Finish books (no books added)
                   "n\n" +            // No address
                   "n\n" +            // Not urgent
                   "\n" +             // Blank payment
                   "\n" +             // Blank comment
                   "n\n";             // No notification

    ConsoleInputHelper inputHelper = createInputHelper(input);
    CreateOrderCommand command = new CreateOrderCommand(catalogService, inputHelper);
    
    assertDoesNotThrow(command::execute);
    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Order Successfully Created"));
  }

  @Test
  void executeShouldHandleAddressWithBlankOptionals() {
    catalogService.addBook("Dune", "Frank Herbert", 1965, BookGenre.SCIENCE_FICTION);
    String input = "ORD-004\n" +      // Order ID
                   "Max\n" +          // Customer Name
                   "0\n" +            // Finish books
                   "y\n" +            // Yes to address
                   "UA\n" +           // Country
                   "Lviv\n" +         // City
                   "Franka\n" +       // Street
                   "2\n" +            // Building
                   "\n" +             // Blank apartment
                   "\n" +             // Blank postal code
                   "\n" +             // Blank details
                   "n\n" +            // Not urgent
                   "\n" +             // Blank payment
                   "\n" +             // Blank comment
                   "n\n";             // No notification

    ConsoleInputHelper inputHelper = createInputHelper(input);
    CreateOrderCommand command = new CreateOrderCommand(catalogService, inputHelper);
    
    assertDoesNotThrow(command::execute);
    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Order Successfully Created"));
  }

  @Test
  void executeShouldHandleIllegalArgumentException() {
    // Empty Order ID throws IllegalArgumentException in readNonEmptyText
    String input = "\n"; 
    ConsoleInputHelper inputHelper = createInputHelper(input);
    CreateOrderCommand command = new CreateOrderCommand(catalogService, inputHelper);
    
    assertDoesNotThrow(command::execute);
    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Error creating order:"));
  }

  private ConsoleInputHelper createInputHelper(String input) {
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    return new ConsoleInputHelper(new Scanner(inputStream));
  }
}
