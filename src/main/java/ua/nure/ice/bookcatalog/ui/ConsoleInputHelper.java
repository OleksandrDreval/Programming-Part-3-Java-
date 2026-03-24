package ua.nure.ice.bookcatalog.ui;

import java.util.Scanner;
import ua.nure.ice.bookcatalog.model.BookGenre;

public class ConsoleInputHelper {
  private final Scanner scanner;

  public ConsoleInputHelper(Scanner scanner) {
    this.scanner = scanner;
  }

  public String readNonEmptyText(String prompt) {
    System.out.print(prompt);
    String input = scanner.nextLine();
    if (input == null || input.isBlank()) {
      throw new IllegalArgumentException("Input must not be empty.");
    }
    return input.trim();
  }

  public int readInt(String prompt) {
    System.out.print(prompt);
    String input = scanner.nextLine();
    try {
      return Integer.parseInt(input.trim());
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("You must enter a valid integer number.");
    }
  }

  public long readLong(String prompt) {
    System.out.print(prompt);
    String input = scanner.nextLine();
    try {
      return Long.parseLong(input.trim());
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("You must enter a valid long number.");
    }
  }

  public BookGenre readGenre() {
    System.out.println("Choose a genre from list:");
    BookGenre[] genres = BookGenre.values();
    for (int index = 0; index < genres.length; index++) {
      System.out.println((index + 1) + ". " + genres[index]);
    }

    int choice = readInt("Your choice: ");
    if (choice < 1 || choice > genres.length) {
      throw new IllegalArgumentException("Invalid genre number.");
    }

    return genres[choice - 1];
  }
}
