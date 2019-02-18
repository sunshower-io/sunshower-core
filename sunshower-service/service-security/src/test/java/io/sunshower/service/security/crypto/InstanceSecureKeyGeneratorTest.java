package io.sunshower.service.security.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.vault.KeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstanceSecureKeyGeneratorTest {
  KeyProvider provider;

  @BeforeEach
  void setUp() {
    provider = new InstanceSecureKeyGenerator(mock(EncryptionService.class));
  }

  @Test
  void ensureKeyGeneratorWorks() {
    assertNotNull(provider.secureString(10));
  }

  @Test
  void ensureStringLengthIsCorrect() {
    assertThat(provider.getKey().length(), is(44));
  }
}
