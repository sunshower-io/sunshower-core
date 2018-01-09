package io.sunshower.core.security;

/** Created by haswell on 10/20/16. */
public class InvalidTokenException extends SecurityException {

  public InvalidTokenException() {}

  public InvalidTokenException(String message) {
    super(message);
  }

  public InvalidTokenException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidTokenException(Throwable cause) {
    super(cause);
  }

  public InvalidTokenException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
