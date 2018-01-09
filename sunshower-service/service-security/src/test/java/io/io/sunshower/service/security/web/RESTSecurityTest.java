package io.io.sunshower.service.security.web;

import static org.junit.Assert.fail;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.core.security.InvalidTokenException;
import io.sunshower.model.core.auth.Token;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.service.signup.SignupService;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

public class RESTSecurityTest extends SecurityTest {

  static Logger logger = Logger.getLogger(RESTSecurityTest.class.getName());

  @Inject private SignupService signupService;

  @Inject private SignupService authenticatedSignupService;

  @Inject private AuthenticationService authenticationService;

  @Test
  public void ensureAttemptingToAccessValidateThrowsException() throws InterruptedException {
    try {
      authenticationService.validate(new Token("frap", null));
      fail("Expected exception");
    } catch (InvalidTokenException ex) {
    }
  }

  @Test
  public void ensureAttemptingToAccessSecuredEndpointFails() throws InterruptedException {
    User u = new User();
    u.setUsername("josiah");
    u.setPassword("password");
    u.getDetails().setEmailAddress("joe16@email.com");
    signupService.signup(u);

    try {

      signupService.list();
      fail("Not authenticated");
    } catch (AuthenticationCredentialsNotFoundException ex) {

    }
  }

  @Test
  @WithMockUser(username = "user", password = "cool", authorities = "admin")
  public void ensureAttemptingToAccessSecuredEndpointAfterAuthenticationSucceeds() {
    List<User> users = authenticatedSignupService.list();
  }

  @Test
  @WithMockUser(username = "user", password = "cool", authorities = "admin")
  public void ensureMultipleClientsCantAccessSimultaneously() {
    signupService.list();
  }
}
