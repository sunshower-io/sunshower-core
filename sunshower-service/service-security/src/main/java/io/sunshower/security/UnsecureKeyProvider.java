package io.sunshower.security;

import io.sunshower.encodings.Base58;
import io.sunshower.model.core.vault.KeyProvider;
import java.util.Random;
import lombok.val;

/** Don't substitute this for the default application key provider for all the tea in China. */
public class UnsecureKeyProvider implements KeyProvider {
  static final Random random = new Random();

  @Override
  public String secureString(int length) {
    val bytes = new byte[length];
    random.nextBytes(bytes);
    return Base58.getInstance(Base58.Alphabets.Default).encode(bytes);
  }

  @Override
  public String getKey() {
    return secureString(32);
  }

  @Override
  public String regenerate() {
    return "hello";
  }
}
