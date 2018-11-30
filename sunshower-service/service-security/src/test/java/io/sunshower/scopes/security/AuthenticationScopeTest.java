package io.sunshower.scopes.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.sunshower.common.Identifier;
import io.sunshower.service.security.Session;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;

class AuthenticationScopeTest {

  Session session;
  AnnotationConfigApplicationContext context;
  MockAuthenticationScopedSessionConfig configuration;

  @BeforeEach
  public void setUp() {
    session = mock(Session.class);
    context = new AnnotationConfigApplicationContext();
    configuration = new MockAuthenticationScopedSessionConfig(session);
    context.registerBean(
        UUID.randomUUID().toString(),
        MockAuthenticationScopedSessionConfig.class,
        () -> configuration);
  }

  @Test
  void ensureRetrievingAuthenticationScopedBeanWorksForSingleValue() {
    context.refresh();
    when(session.getUsername()).thenReturn("wab");
    val uid1 = Identifier.random();
    val uid2 = Identifier.random();
    when(session.getId()).thenReturn(uid1);
    String bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("wab"));
    context.stop();
  }

  @Test
  void ensureRetrievingAuthenticationScopedObjectWorksForMultipleValues() {
    context.refresh();
    when(session.getUsername()).thenReturn("wab");
    val uid1 = Identifier.random();
    val uid2 = Identifier.random();
    when(session.getId()).thenReturn(uid1);

    String bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("wab"));

    when(session.getUsername()).thenReturn("dab");
    when(session.getId()).thenReturn(uid2);
    bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("dab"));
  }
}
