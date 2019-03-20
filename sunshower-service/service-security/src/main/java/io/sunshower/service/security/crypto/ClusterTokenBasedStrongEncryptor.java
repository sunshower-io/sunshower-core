package io.sunshower.service.security.crypto;

import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.vault.KeyProvider;
import javax.inject.Provider;
import lombok.val;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.text.TextEncryptor;

public class ClusterTokenBasedStrongEncryptor implements TextEncryptor {

  final Object lock = new Object();

  private final KeyProvider provider;
  private volatile TextEncryptor encryptor;
  private final Provider<EncryptionService> service;

  public ClusterTokenBasedStrongEncryptor(
      Provider<EncryptionService> encryptionService, KeyProvider provider) {
    this.provider = provider;
    this.service = encryptionService;
  }

  @Override
  public String encrypt(String message) {
    return getEncryptor().encrypt(message);
  }

  @Override
  public String decrypt(String encryptedMessage) {
    return getEncryptor().decrypt(encryptedMessage);
  }

  private TextEncryptor getEncryptor() {
    TextEncryptor lenc = encryptor;
    if (lenc == null) {
      synchronized (lock) {
        lenc = encryptor;
        if (lenc == null) {
          val svc = service.get();
          //          val token = svc.getClusterToken().getToken();

          val encryptor = new PooledPBEStringEncryptor();
          //          val saltGenerator = new StringFixedSaltGenerator(token);
          val saltGenerator = new RandomSaltGenerator();
          encryptor.setSaltGenerator(saltGenerator);
          encryptor.setPassword(provider.getKey());
          encryptor.setPoolSize(10);
          this.encryptor = lenc = new DelegatingTextEncryptor(encryptor);
        }
      }
    }
    return lenc;
  }
}
