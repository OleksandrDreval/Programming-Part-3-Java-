package ua.nure.ice.bookcatalog.exception;

public class DuplicateBookException extends RuntimeException {
  public DuplicateBookException(String message) {
    super(message);
  }
}
