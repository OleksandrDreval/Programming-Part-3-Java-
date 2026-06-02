package ua.nure.ice.bookcatalog.practical4.exception;

public class InvalidBookDataException extends RuntimeException {
  public InvalidBookDataException(String message) {
    super(message);
  }
}
