package ua.nure.ice.bookcatalog.laboratorna1.app;

import ua.nure.ice.bookcatalog.laboratorna1.repository.BookRepository;
import ua.nure.ice.bookcatalog.laboratorna1.repository.InMemoryBookRepository;
import ua.nure.ice.bookcatalog.laboratorna1.service.CatalogService;
import ua.nure.ice.bookcatalog.laboratorna1.ui.ConsoleUI;

public class App {
  public static void main(String[] args) {
    BookRepository bookRepository = new InMemoryBookRepository();
    CatalogService catalogService = new CatalogService(bookRepository);
    ConsoleUI consoleUI = new ConsoleUI(catalogService);
    consoleUI.start();
  }
}
