package ua.nure.ice.bookcatalog.laboratorna1.ui.command;

import ua.nure.ice.bookcatalog.laboratorna1.service.CatalogService;
import ua.nure.ice.bookcatalog.laboratorna1.ui.ConsoleInputHelper;

public class RemoveBookCommand implements MenuCommand {
  private final CatalogService catalogService;
  private final ConsoleInputHelper inputHelper;

  public RemoveBookCommand(CatalogService catalogService, ConsoleInputHelper inputHelper) {
    this.catalogService = catalogService;
    this.inputHelper = inputHelper;
  }

  @Override
  public void execute() {
    System.out.println("Remove book");
    System.out.println("----------------------------------------");

    long id = inputHelper.readLong("Enter book id to remove: ");
    catalogService.removeBook(id);
    System.out.println("Book was removed successfully.");
    System.out.println("Removed book id: " + id);
  }

  @Override
  public String getTitle() {
    return "Remove book";
  }
}
