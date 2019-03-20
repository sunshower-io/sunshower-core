package io.sunshower.service.security;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.signup.SignupService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.val;
import org.junit.jupiter.api.Test;

public class DefaultAuthenticationServiceTest extends SecurityTest {

  @Inject private SignupService signupService;
  @PersistenceContext private EntityManager entityManager;
  @Inject private AuthenticationService authenticationService;

  @Test
  public void ensureAuthenticationWorks() {
    assertThat(authenticationService, is(not(nullValue())));
  }

  @Test
  void ensureLoggingInAsUserWorks() {
    val user = new User();
    user.setUsername("test");
    user.setPassword("password");
    user.getDetails().setEmailAddress("test@whatever.com");
    signupService.signup(user);
    authenticationService.authenticate(user);
    val u = entityManager.find(User.class, user.getId());
    assertThat(u.getDetails().getLoginCount(), is(0));
  }
}
