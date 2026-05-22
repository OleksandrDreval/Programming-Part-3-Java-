package ua.nure.ice.bookcatalog.practice3.exception;

public class InvalidBookDataException extends RuntimeException {
  public InvalidBookDataException(String message) {
    super(message);
  }
}
