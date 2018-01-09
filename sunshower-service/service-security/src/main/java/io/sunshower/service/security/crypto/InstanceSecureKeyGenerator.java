package io.sunshower.service.security.crypto;

import io.sunshower.model.core.vault.KeyProvider;
import java.security.SecureRandom;
import java.util.Base64;
import javax.inject.Singleton;
import org.springframework.stereotype.Service;

/** Created by haswell on 10/20/16. */
@Service
@Singleton
public class InstanceSecureKeyGenerator implements KeyProvider {

  static final String key = generateKey();

  public InstanceSecureKeyGenerator() {}

  @Override
  public String getKey() {
    return key;
  }

  static final String generateKey() {
    final SecureRandom random = new SecureRandom();
    final byte[] bytes = random.generateSeed(32);
    return Base64.getEncoder().encodeToString(bytes);
  }
}
