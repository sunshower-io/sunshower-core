package io.sunshower.service.security.crypto;

import io.sunshower.encodings.Base58;
import io.sunshower.model.core.vault.KeyProvider;
import java.security.SecureRandom;
import java.util.Base64;
import javax.inject.Singleton;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@Singleton
public class InstanceSecureKeyGenerator implements KeyProvider {

  static String key;
  static final SecureRandom random = new SecureRandom();

  static {
    generateKey();
  }

  public InstanceSecureKeyGenerator() {}

  @Override
  public String secureString(int length) {
    val next = new byte[length];
    random.nextBytes(next);
    return Base58.getInstance(Base58.Alphabets.Default).encode(next);
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String regenerate() {
    return generateKey();
  }

  static final String generateKey() {
    final byte[] bytes = random.generateSeed(32);
    return (key = Base64.getEncoder().encodeToString(bytes));
  }
}
