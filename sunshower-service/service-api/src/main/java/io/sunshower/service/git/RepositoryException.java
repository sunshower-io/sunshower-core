package io.sunshower.service.git;

import io.sunshower.model.core.faults.SystemException;

/** Created by haswell on 5/22/17. */
public class RepositoryException extends SystemException {
  public RepositoryException() {
    super();
  }

  public RepositoryException(String message) {
    super(message);
  }

  public RepositoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public RepositoryException(Throwable cause) {
    super(cause);
  }

  protected RepositoryException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
