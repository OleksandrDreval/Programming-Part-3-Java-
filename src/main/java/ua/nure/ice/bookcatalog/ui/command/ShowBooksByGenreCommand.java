package ua.nure.ice.bookcatalog.ui.command;

import java.util.List;
import ua.nure.ice.bookcatalog.model.Book;
import ua.nure.ice.bookcatalog.model.BookGenre;
import ua.nure.ice.bookcatalog.service.CatalogService;
import ua.nure.ice.bookcatalog.ui.ConsoleInputHelper;

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
    if (books.isEmpty()) {
      System.out.println("No books found for selected genre.");
      return;
    }

    System.out.println("Books in genre " + genre + ":");
    for (Book book : books) {
      System.out.println(book);
    }
  }

  @Override
  public String getTitle() {
    return "Show books by genre";
  }
}
