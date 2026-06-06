package ua.nure.ice.bookcatalog.laboratorna4.exception;

public class DuplicateBookException extends RuntimeException {
  public DuplicateBookException(String message) {
    super(message);
  }
}
