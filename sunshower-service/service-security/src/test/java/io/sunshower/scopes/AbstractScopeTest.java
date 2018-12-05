package io.sunshower.scopes;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Configuration;
import io.sunshower.scopes.conversation.ConversationScope;
import io.sunshower.scopes.security.AuthenticationScope;
import io.sunshower.service.security.Session;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AbstractScopeTest {
  protected Session session;
  protected Configuration cfg;
  protected AnnotationConfigApplicationContext context;
  protected ApplicationEventPublisher publisher;
  protected MockAuthenticationScopedSessionConfig configuration;
  protected AuthenticationScope authenticationScope;
  protected ConversationScope conversationScope;

  @BeforeEach
  public void setUp() {
    MockAuthenticationScopedSessionConfig.called = 0;
    cfg = mock(Configuration.class);
    session = mock(Session.class);
    doReturn(cfg).when(session).getUserConfiguration();
    context = new AnnotationConfigApplicationContext();
    configuration = new MockAuthenticationScopedSessionConfig(session);
    context.registerBean(
        UUID.randomUUID().toString(),
        MockAuthenticationScopedSessionConfig.class,
        () -> configuration);
  }

  protected void stop() {
    context.close();
  }

  protected void doSetup() {
    when(session.getUsername()).thenReturn("wab");
    val uid1 = Identifier.random();
    when(session.getId()).thenReturn(uid1);
  }

  protected void start() {
    context.refresh();
    initPublisher();
  }

  protected void initPublisher() {
    if (!context.isRunning()) {
      throw new IllegalStateException("context is not running.  Call start()");
    }
    publisher = context;

    conversationScope = configuration.getConversationScope();
    authenticationScope = configuration.getAuthenticationScope();
  }
}
