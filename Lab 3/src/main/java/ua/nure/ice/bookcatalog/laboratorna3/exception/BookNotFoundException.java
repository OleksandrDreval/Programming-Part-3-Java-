package ua.nure.ice.bookcatalog.laboratorna3.exception;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}
