package ua.nure.ice.bookcatalog.laboratorna1.ui.command;

import ua.nure.ice.bookcatalog.laboratorna1.model.Book;
import ua.nure.ice.bookcatalog.laboratorna1.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna1.service.CatalogService;
import ua.nure.ice.bookcatalog.laboratorna1.ui.ConsoleInputHelper;

public class AddBookCommand implements MenuCommand {
  private final CatalogService catalogService;
  private final ConsoleInputHelper inputHelper;

  public AddBookCommand(CatalogService catalogService, ConsoleInputHelper inputHelper) {
    this.catalogService = catalogService;
    this.inputHelper = inputHelper;
  }

  @Override
  public void execute() {
    System.out.println("Add new book");
    System.out.println("----------------------------------------");

    String title = inputHelper.readNonEmptyText("Enter book title: ");
    String author = inputHelper.readNonEmptyText("Enter author: ");
    int publicationYear = inputHelper.readInt("Enter publication year: ");
    BookGenre genre = inputHelper.readGenre();

    Book book = catalogService.addBook(title, author, publicationYear, genre);
    System.out.println("Book was added successfully.");
    System.out.println("Created book id: " + book.getId());
  }

  @Override
  public String getTitle() {
    return "Add book";
  }
}
