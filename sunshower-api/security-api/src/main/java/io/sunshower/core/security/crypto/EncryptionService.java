package io.sunshower.core.security.crypto;

import io.sunshower.model.core.auth.ClusterToken;
import io.sunshower.model.core.auth.User;

public interface EncryptionService {

  String sign(String value);

  String unsign(String value);

  String encrypt(String password);

  boolean matches(String raw, String password);

  String createToken(User user);

  User findByToken(String token);

  ClusterToken getClusterToken();

  ClusterToken refreshClusterToken();
}
