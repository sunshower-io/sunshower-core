package io.sunshower.service.security;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.service.signup.SignupService;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

public class DefaultAuthenticationServiceTest extends SecurityTest {

  @Inject private SignupService signupService;

  @Inject private AuthenticationService authenticationService;

  @Test
  public void ensureAuthenticationWorks() {
    assertThat(authenticationService, is(not(nullValue())));
  }
}
