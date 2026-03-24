package ua.nure.ice.bookcatalog.ui.command;

import ua.nure.ice.bookcatalog.model.Book;
import ua.nure.ice.bookcatalog.model.BookGenre;
import ua.nure.ice.bookcatalog.service.CatalogService;
import ua.nure.ice.bookcatalog.ui.ConsoleInputHelper;

public class AddBookCommand implements MenuCommand {
  private final CatalogService catalogService;
  private final ConsoleInputHelper inputHelper;

  public AddBookCommand(CatalogService catalogService, ConsoleInputHelper inputHelper) {
    this.catalogService = catalogService;
    this.inputHelper = inputHelper;
  }

  @Override
  public void execute() {
    String title = inputHelper.readNonEmptyText("Enter book title: ");
    String author = inputHelper.readNonEmptyText("Enter author: ");
    int publicationYear = inputHelper.readInt("Enter publication year: ");
    BookGenre genre = inputHelper.readGenre();

    Book book = catalogService.addBook(title, author, publicationYear, genre);
    System.out.println("Book was added successfully with id: " + book.getId());
  }

  @Override
  public String getTitle() {
    return "Add book";
  }
}
