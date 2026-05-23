package ua.nure.ice.bookcatalog.practice3.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import ua.nure.ice.bookcatalog.practice3.service.CatalogService;
import ua.nure.ice.bookcatalog.practice3.ui.command.AddBookCommand;
import ua.nure.ice.bookcatalog.practice3.ui.command.ExitCommand;
import ua.nure.ice.bookcatalog.practice3.ui.command.MenuCommand;
import ua.nure.ice.bookcatalog.practice3.ui.command.RemoveBookCommand;
import ua.nure.ice.bookcatalog.practice3.ui.command.ShowAllBooksCommand;
import ua.nure.ice.bookcatalog.practice3.ui.command.ShowBooksByGenreCommand;
import ua.nure.ice.bookcatalog.practice3.ui.command.CreateOrderCommand;

public class ConsoleUI {
  private static final String SECTION_SEPARATOR = "========================================";
  private static final String BLOCK_SEPARATOR = "----------------------------------------";

  private final Scanner scanner;
  private final Map<String, MenuCommand> commandByOption;
  private boolean running;

  public ConsoleUI(CatalogService catalogService) {
    this.scanner = new Scanner(System.in);
    ConsoleInputHelper inputHelper = new ConsoleInputHelper(scanner);

    this.commandByOption = new LinkedHashMap<>();
    this.commandByOption.put("1", new AddBookCommand(catalogService, inputHelper));
    this.commandByOption.put("2", new RemoveBookCommand(catalogService, inputHelper));
    this.commandByOption.put("3", new ShowAllBooksCommand(catalogService));
    this.commandByOption.put("4", new ShowBooksByGenreCommand(catalogService, inputHelper));
    this.commandByOption.put("5", new CreateOrderCommand(catalogService, inputHelper));
    this.commandByOption.put("0", new ExitCommand(this::stop));

    this.running = true;
  }

  public void start() {
    printWelcome();

    while (running) {
      printMenu();
      String choice = scanner.nextLine().trim();

      MenuCommand command = commandByOption.get(choice);
      if (command == null) {
        System.out.println(BLOCK_SEPARATOR);
        System.out.println("Unknown command. Try again.");
        System.out.println();
        continue;
      }

      try {
        System.out.println(BLOCK_SEPARATOR);
        command.execute();
        if (running) {
          System.out.println(BLOCK_SEPARATOR);
          System.out.println("Action completed.");
        }
      } catch (RuntimeException exception) {
        System.out.println(BLOCK_SEPARATOR);
        System.out.println("Error: " + exception.getMessage());
      }

      System.out.println();
    }

    System.out.println(SECTION_SEPARATOR);
    System.out.println("Goodbye.");
    System.out.println(SECTION_SEPARATOR);

    scanner.close();
  }

  private void printMenu() {
    System.out.println(SECTION_SEPARATOR);
    System.out.println("BOOK CATALOG MENU");
    System.out.println(SECTION_SEPARATOR);
    for (Map.Entry<String, MenuCommand> entry : commandByOption.entrySet()) {
      System.out.println(entry.getKey() + ". " + entry.getValue().getTitle());
    }
    System.out.println(BLOCK_SEPARATOR);
    System.out.print("Choose action: ");
  }

  private void printWelcome() {
    System.out.println(SECTION_SEPARATOR);
    System.out.println("Welcome to Book Catalog");
    System.out.println(SECTION_SEPARATOR);
  }

  private void stop() {
    running = false;
  }
}
