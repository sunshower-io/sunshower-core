package io.sunshower.service.security.crypto;

import io.sunshower.core.security.crypto.EncryptionService;
import javax.inject.Provider;
import lombok.val;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

public class ClusterTokenBasedStrongEncryptor implements TextEncryptor {

  final Object lock = new Object();

  private volatile StrongTextEncryptor encryptor;
  private final Provider<EncryptionService> service;

  public ClusterTokenBasedStrongEncryptor(Provider<EncryptionService> encryptionService) {
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

  private StrongTextEncryptor getEncryptor() {
    StrongTextEncryptor lenc = encryptor;
    if (lenc == null) {
      synchronized (lock) {
        lenc = encryptor;
        if (lenc == null) {
          val svc = service.get();
          encryptor = (lenc = new StrongTextEncryptor());
          encryptor.setPassword(svc.getClusterToken().getToken());
        }
      }
    }
    return lenc;
  }
}
