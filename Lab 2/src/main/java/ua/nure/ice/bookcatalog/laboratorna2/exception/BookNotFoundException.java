package ua.nure.ice.bookcatalog.laboratorna2.exception;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}
