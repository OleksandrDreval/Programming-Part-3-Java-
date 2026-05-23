package ua.nure.ice.bookcatalog.laboratorna1.exception;

public class DuplicateBookException extends RuntimeException {
  public DuplicateBookException(String message) {
    super(message);
  }
}
