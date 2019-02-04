package io.sunshower.scopes.conversation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import io.sunshower.scopes.AbstractScopeTest;
import io.sunshower.scopes.NoActiveConversationException;
import io.sunshower.service.conversation.KeyedConversationFilter;
import javax.ws.rs.container.ContainerRequestContext;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConversationScopeTest extends AbstractScopeTest {

  @Mock private ContainerRequestContext requestContext;

  @BeforeEach
  public void setUp() {
    super.setUp();
    start();
    doReturn("conversationid:initiated")
        .when(requestContext)
        .getHeaderString(KeyedConversationFilter.CONVERSATION_HEADER_KEY);
  }

  @AfterEach
  void tearDown() {
    stop();
  }

  @Test
  void ensureFiringLogoutEventWorks() {
    val event = new ConversationInitiatedEvent("hello");
    publisher.publishEvent(event);
  }

  @Test
  void ensureConversationScopedBeanWithoutActiveConversationFails() {
    assertThrows(
        NoActiveConversationException.class,
        () -> {
          context.getBean("conversationScopedBean", String.class);
        });
  }

  @Test
  void ensureConversationScopedWithWithActiveConversationWorks() {
    ConversationHolder.pushConversation(
        new ConversationContext("hello", ConversationState.Initiated));
    val event = new ConversationInitiatedEvent("hello");
    publisher.publishEvent(event);
    val bean = context.getBean("conversationScopedBean", String.class);
    ConversationHolder.popConversation();
  }
}
