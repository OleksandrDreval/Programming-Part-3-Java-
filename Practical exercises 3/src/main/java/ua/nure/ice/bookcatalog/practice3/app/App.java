package ua.nure.ice.bookcatalog.practice3.app;

import ua.nure.ice.bookcatalog.practice3.repository.BookRepository;
import ua.nure.ice.bookcatalog.practice3.repository.InMemoryBookRepository;
import ua.nure.ice.bookcatalog.practice3.service.CatalogService;
import ua.nure.ice.bookcatalog.practice3.ui.ConsoleUI;

public class App {
  public static void main(String[] args) {
    BookRepository bookRepository = new InMemoryBookRepository();
    CatalogService catalogService = new CatalogService(bookRepository);
    ConsoleUI consoleUI = new ConsoleUI(catalogService);
    consoleUI.start();
  }
}
