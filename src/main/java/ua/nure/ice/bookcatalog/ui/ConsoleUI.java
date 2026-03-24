package ua.nure.ice.bookcatalog.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import ua.nure.ice.bookcatalog.service.CatalogService;
import ua.nure.ice.bookcatalog.ui.command.AddBookCommand;
import ua.nure.ice.bookcatalog.ui.command.ExitCommand;
import ua.nure.ice.bookcatalog.ui.command.MenuCommand;
import ua.nure.ice.bookcatalog.ui.command.RemoveBookCommand;
import ua.nure.ice.bookcatalog.ui.command.ShowAllBooksCommand;
import ua.nure.ice.bookcatalog.ui.command.ShowBooksByGenreCommand;

public class ConsoleUI {
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
    this.commandByOption.put("0", new ExitCommand(this::stop));

    this.running = true;
  }

  public void start() {
    while (running) {
      printMenu();
      String choice = scanner.nextLine().trim();

      MenuCommand command = commandByOption.get(choice);
      if (command == null) {
        System.out.println("Unknown command. Try again.");
        System.out.println();
        continue;
      }

      try {
        command.execute();
      } catch (RuntimeException exception) {
        System.out.println("Error: " + exception.getMessage());
      }

      System.out.println();
    }

    scanner.close();
  }

  private void printMenu() {
    System.out.println("===== BOOK CATALOG =====");
    for (Map.Entry<String, MenuCommand> entry : commandByOption.entrySet()) {
      System.out.println(entry.getKey() + ". " + entry.getValue().getTitle());
    }
    System.out.print("Choose action: ");
  }

  private void stop() {
    running = false;
  }
}
