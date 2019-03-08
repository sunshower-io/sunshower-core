package io.sunshower.scopes.conversation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.security.UnsecureKeyProvider;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DelimiterSeparatedConversationSerializerTest {

  private KeyProvider provider;
  private Conversation conversation;
  private ConversationSerializer serializer;

  @BeforeEach
  public void setUp() {
    provider = new UnsecureKeyProvider();
    serializer = new DelimiterSeparatedConversationSerializer(':');
  }

  @Test
  void ensureSerializerReturnsCorrectStateForSimpleId() {
    conversation = serializer.parse("initiated");
    assertThat(conversation.getState(), is(ConversationState.Initiated));
  }

  @Test
  void ensureSerializerReturnsCorrectStateForPrefixedState() {
    val str = provider.secureString(10);
    conversation = serializer.parse(String.format("%s:%s", str, "finalized"));
    assertThat(conversation.getState(), is(ConversationState.Finalized));
  }

  @Test
  void ensureSerializerFailsOnIdWithInitiated() {
    val str = provider.secureString(10);
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          conversation = serializer.parse(String.format("%s:%s", str, "initiated"));
        });
  }
}
