package ua.nure.ice.bookcatalog.ui.command;

import ua.nure.ice.bookcatalog.service.CatalogService;
import ua.nure.ice.bookcatalog.ui.ConsoleInputHelper;

public class RemoveBookCommand implements MenuCommand {
  private final CatalogService catalogService;
  private final ConsoleInputHelper inputHelper;

  public RemoveBookCommand(CatalogService catalogService, ConsoleInputHelper inputHelper) {
    this.catalogService = catalogService;
    this.inputHelper = inputHelper;
  }

  @Override
  public void execute() {
    long id = inputHelper.readLong("Enter book id to remove: ");
    catalogService.removeBook(id);
    System.out.println("Book was removed successfully.");
  }

  @Override
  public String getTitle() {
    return "Remove book";
  }
}
