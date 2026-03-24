package ua.nure.ice.bookcatalog.app;

import ua.nure.ice.bookcatalog.repository.BookRepository;
import ua.nure.ice.bookcatalog.repository.InMemoryBookRepository;
import ua.nure.ice.bookcatalog.service.CatalogService;
import ua.nure.ice.bookcatalog.ui.ConsoleUI;

public class App {
  public static void main(String[] args) {
    BookRepository bookRepository = new InMemoryBookRepository();
    CatalogService catalogService = new CatalogService(bookRepository);
    ConsoleUI consoleUI = new ConsoleUI(catalogService);
    consoleUI.start();
  }
}
