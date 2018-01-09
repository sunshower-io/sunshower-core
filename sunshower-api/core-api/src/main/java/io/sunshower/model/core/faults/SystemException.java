package io.sunshower.model.core.faults;

/** Created by haswell on 2/26/17. */
public class SystemException extends RuntimeException {

  public SystemException() {
    super();
  }

  public SystemException(String message) {
    super(message);
  }

  public SystemException(String message, Throwable cause) {
    super(message, cause);
  }

  public SystemException(Throwable cause) {
    super(cause);
  }

  protected SystemException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
