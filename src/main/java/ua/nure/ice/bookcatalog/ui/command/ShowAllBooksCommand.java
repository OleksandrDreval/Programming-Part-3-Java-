package ua.nure.ice.bookcatalog.ui.command;

import java.util.List;
import ua.nure.ice.bookcatalog.model.Book;
import ua.nure.ice.bookcatalog.service.CatalogService;

public class ShowAllBooksCommand implements MenuCommand {
  private final CatalogService catalogService;

  public ShowAllBooksCommand(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @Override
  public void execute() {
    List<Book> books = catalogService.findAllBooks();
    if (books.isEmpty()) {
      System.out.println("Catalog is empty.");
      return;
    }

    System.out.println("All books in catalog:");
    for (Book book : books) {
      System.out.println(book);
    }
  }

  @Override
  public String getTitle() {
    return "Show all books";
  }
}
