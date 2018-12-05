package io.sunshower.scopes.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Configuration;
import io.sunshower.service.security.Session;
import java.util.UUID;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;

class AuthenticationScopeTest {

  Session session;
  Configuration cfg;
  AnnotationConfigApplicationContext context;
  MockAuthenticationScopedSessionConfig configuration;

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

  @Test
  @SneakyThrows
  void ensureTimeoutWorks() {
    context.refresh();
    when(session.getUsername()).thenReturn("wab");
    val uid1 = Identifier.random();
    when(session.getId()).thenReturn(uid1);
    doReturn(50).when(cfg).getValue(anyString());
    String bean = (String) context.getBean("authenticationScopedBean");

    assertThat(bean, is("wab0"));
    Thread.sleep(100);
    bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("wab1"));
    context.stop();
  }

  @Test
  @SneakyThrows
  void ensureTimeoutWorksForNonTimedOutValue() {
    context.refresh();
    when(session.getUsername()).thenReturn("wab");
    val uid1 = Identifier.random();
    when(session.getId()).thenReturn(uid1);
    doReturn(50).when(cfg).getValue(anyString());
    String bean = (String) context.getBean("authenticationScopedBean");

    assertThat(bean, is("wab0"));
    Thread.sleep(25);
    bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("wab0"));
    context.stop();

  }

  @Test
  void ensureRetrievingAuthenticationScopedBeanWorksForSingleValue() {
    context.refresh();
    when(session.getUsername()).thenReturn("wab");
    val uid1 = Identifier.random();
    val uid2 = Identifier.random();
    when(session.getId()).thenReturn(uid1);
    String bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("wab0"));
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
    assertThat(bean, is("wab0"));

    when(session.getUsername()).thenReturn("dab");
    when(session.getId()).thenReturn(uid2);
    bean = (String) context.getBean("authenticationScopedBean");
    assertThat(bean, is("dab1"));
  }
}
