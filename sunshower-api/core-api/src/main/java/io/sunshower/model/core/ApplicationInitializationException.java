package io.sunshower.model.core;

/**
 * Created by haswell on 10/26/16.
 */
public class ApplicationInitializationException extends RuntimeException {

    public ApplicationInitializationException() {
        super();
    }

    public ApplicationInitializationException(String message) {
        super(message);
    }

    public ApplicationInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationInitializationException(Throwable cause) {
        super(cause);
    }

    protected ApplicationInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
