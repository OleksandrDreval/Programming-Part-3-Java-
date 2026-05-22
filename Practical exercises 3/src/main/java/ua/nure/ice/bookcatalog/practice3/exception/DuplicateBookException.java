package ua.nure.ice.bookcatalog.practice3.exception;

public class DuplicateBookException extends RuntimeException {
  public DuplicateBookException(String message) {
    super(message);
  }
}
