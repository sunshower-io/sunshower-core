package io.sunshower.service.security.crypto;

import lombok.AllArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.util.text.TextEncryptor;

@AllArgsConstructor
public class DelegatingTextEncryptor implements TextEncryptor {
  final StringEncryptor encryptor;

  @Override
  public String encrypt(String message) {
    return encryptor.encrypt(message);
  }

  @Override
  public String decrypt(String encryptedMessage) {
    return encryptor.decrypt(encryptedMessage);
  }
}
