package io.sunshower.service.security.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.ClusterToken;
import io.sunshower.model.core.vault.KeyProvider;
import javax.inject.Provider;
import lombok.val;
import org.jasypt.util.text.TextEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClusterTokenBasedStrongEncryptorTest {

  private ClusterToken token;
  private TextEncryptor encryptor;
  private KeyProvider keyProvider;
  private EncryptionService service;

  @BeforeEach
  public void setUp() {
    keyProvider = new InstanceSecureKeyGenerator();
    token = new ClusterToken();
    token.setToken(keyProvider.getKey());
    service = mock(EncryptionService.class);
    doReturn(token).when(service).getClusterToken();
    encryptor = new ClusterTokenBasedStrongEncryptor(new IdentityProvider());
  }

  @Test
  void ensureTokenEncryptorIsAbelian() {
    val id = Identifier.random().toString();
    val encrypted = encryptor.encrypt(id);
    val decrypted = encryptor.decrypt(encrypted);
    assertThat(decrypted, is(id));
  }

  @Test
  void ensureMultipleEncryptorsEncryptSameData() {
    val encrypted = encryptor.encrypt("hello");
    val next = new ClusterTokenBasedStrongEncryptor(new IdentityProvider());
    val enc = next.decrypt(encrypted);
    assertThat("hello", is(enc));
  }

  class IdentityProvider implements Provider<EncryptionService> {

    @Override
    public EncryptionService get() {
      return service;
    }
  }
}
