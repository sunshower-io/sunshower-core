package io.sunshower.service.security.crypto;

public interface Signature {
  String id(String token);

  String token(String input);
}
