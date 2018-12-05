package io.sunshower.scopes.conversation;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.service.security.crypto.InstanceSecureKeyGenerator;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class ConversationStateTest {
    private KeyProvider provider;

    @BeforeEach
    public void setUp() {
        provider = new InstanceSecureKeyGenerator();
    }

    @Test
    void ensureParsingStateFromValueWorks() {
        assertThat(ConversationState.fromString("initiate"), is(ConversationState.Initiated));
    }

    @Test
    void ensureAllStatesAreParseable() {
        for (val state : ConversationState.values()) {
            val s = state.toString().toLowerCase();
            assertThat(ConversationState.fromString(s), is(state));
        }
    }


}