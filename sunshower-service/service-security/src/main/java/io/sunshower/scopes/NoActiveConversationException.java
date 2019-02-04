package io.sunshower.scopes;

public class NoActiveConversationException extends RuntimeException {
  public NoActiveConversationException(String s) {
    super(s);
  }
}
