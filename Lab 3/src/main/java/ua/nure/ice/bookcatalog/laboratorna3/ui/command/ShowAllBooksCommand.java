package ua.nure.ice.bookcatalog.laboratorna3.ui.command;

import java.util.List;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.service.CatalogService;
import ua.nure.ice.bookcatalog.laboratorna3.ui.BookDisplayFormatter;

public class ShowAllBooksCommand implements MenuCommand {
  private final CatalogService catalogService;

  public ShowAllBooksCommand(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @Override
  public void execute() {
    List<Book> books = catalogService.findAllBooks();
    System.out.println("Catalog overview");
    System.out.println("----------------------------------------");

    if (books.isEmpty()) {
      System.out.println("Catalog is empty.");
      return;
    }

    System.out.println("Total books: " + books.size());
    System.out.println("All books in catalog:");
    int index = 1;
    for (Book book : books) {
      System.out.println(index++ + ") " + BookDisplayFormatter.format(book));
    }
  }

  @Override
  public String getTitle() {
    return "Show all books";
  }
}
