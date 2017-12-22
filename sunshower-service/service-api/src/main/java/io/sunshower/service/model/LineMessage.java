package io.sunshower.service.model;

/**
 * Created by haswell on 5/24/17.
 */
public class LineMessage implements Message {
    final String message;

    public LineMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
