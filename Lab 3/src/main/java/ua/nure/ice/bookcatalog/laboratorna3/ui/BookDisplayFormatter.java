package ua.nure.ice.bookcatalog.laboratorna3.ui;

import ua.nure.ice.bookcatalog.laboratorna3.model.Book;

public final class BookDisplayFormatter {
  private BookDisplayFormatter() {
  }

  public static String format(Book book) {
    return "ID: " + book.getId()
        + " | Title: " + book.getTitle()
        + " | Author: " + book.getAuthor()
        + " | Year: " + book.getPublicationYear()
        + " | Genre: " + book.getGenre();
  }
}
