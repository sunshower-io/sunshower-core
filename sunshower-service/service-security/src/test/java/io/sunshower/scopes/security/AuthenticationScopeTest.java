package io.sunshower.scopes.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import io.sunshower.common.Identifier;
import io.sunshower.scopes.AbstractScopeTest;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

class AuthenticationScopeTest extends AbstractScopeTest {

  @Test
  @SneakyThrows
  void ensureTimeoutWorks() {
    context.refresh();
    doSetup();
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
    doSetup();
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
