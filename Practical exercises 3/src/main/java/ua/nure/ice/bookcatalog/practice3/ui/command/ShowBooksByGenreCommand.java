package ua.nure.ice.bookcatalog.practice3.ui.command;

import java.util.List;
import ua.nure.ice.bookcatalog.practice3.model.Book;
import ua.nure.ice.bookcatalog.practice3.model.BookGenre;
import ua.nure.ice.bookcatalog.practice3.service.CatalogService;
import ua.nure.ice.bookcatalog.practice3.ui.BookDisplayFormatter;
import ua.nure.ice.bookcatalog.practice3.ui.ConsoleInputHelper;

public class ShowBooksByGenreCommand implements MenuCommand {
  private final CatalogService catalogService;
  private final ConsoleInputHelper inputHelper;

  public ShowBooksByGenreCommand(CatalogService catalogService, ConsoleInputHelper inputHelper) {
    this.catalogService = catalogService;
    this.inputHelper = inputHelper;
  }

  @Override
  public void execute() {
    BookGenre genre = inputHelper.readGenre();
    List<Book> books = catalogService.findBooksByGenre(genre);

    System.out.println("Genre filter result");
    System.out.println("----------------------------------------");
    System.out.println("Selected genre: " + genre);

    if (books.isEmpty()) {
      System.out.println("No books found for selected genre.");
      return;
    }

    System.out.println("Found books: " + books.size());
    int index = 1;
    for (Book book : books) {
      System.out.println(index++ + ") " + BookDisplayFormatter.format(book));
    }
  }

  @Override
  public String getTitle() {
    return "Show books by genre";
  }
}
