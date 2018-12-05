package io.sunshower.service.security.crypto;

import io.sunshower.model.core.vault.KeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstanceSecureKeyGeneratorTest {
    KeyProvider provider;

    @BeforeEach
    void setUp() {
        provider = new InstanceSecureKeyGenerator();
    }

    @Test
    void ensureKeyGeneratorWorks() {
        assertNotNull(provider.secureString(10));
    }

}