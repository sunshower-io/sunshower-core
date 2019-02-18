package io.sunshower.service.security.crypto;

import io.sunshower.core.security.crypto.EncryptionService;
import javax.inject.Provider;
import lombok.val;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

public class ClusterTokenBasedStrongEncryptor implements TextEncryptor {

  private final Provider<EncryptionService> service;

  public ClusterTokenBasedStrongEncryptor(Provider<EncryptionService> encryptionService) {
    this.service = encryptionService;
  }

  @Override
  public String encrypt(String message) {
    StrongTextEncryptor t = getEncryptor();
    return t.encrypt(message);
  }

  @Override
  public String decrypt(String encryptedMessage) {
    return getEncryptor().decrypt(encryptedMessage);
  }

  private StrongTextEncryptor getEncryptor() {
    val svc = service.get();
    val t = new StrongTextEncryptor();
    t.setPassword(svc.getClusterToken().getToken());
    return t;
  }
}
