package io.sunshower.service.security;

public interface TokenManager {

  void checkEncoded(String token);

  void check(String token);
}
