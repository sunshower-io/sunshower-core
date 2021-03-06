package io.io.sunshower.service.security;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.core.security.InvalidCredentialException;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Authentication;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.service.signup.RegistrationRequest;
import io.sunshower.service.signup.SignupService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;

@Transactional
class AuthenticationTest extends SecurityTest {

  @Inject private SignupService service;

  @PersistenceContext private EntityManager entityManager;

  @Inject private EncryptionService encryptionService;

  @Inject private AuthenticationService authenticationService;

  @Test
  @Rollback
  public void ensureSigningUpAndLoggingInWithSignedUpUserWorks() {
    final User user = new User();
    user.setUsername("Josiah1");
    user.setPassword("password1234");
    user.getDetails().setEmailAddress("josiah@whatever1");
    User u = service.signup(user).getUser();
    assertThat(u.getPassword(), is(not("password1234")));
  }

  @Test
  @Rollback
  @WithMockUser(username = "admin", authorities = "admin")
  public void ensureAuthenticatingWithRawUserWorks() {

    final User user = new User();
    user.setUsername("Josiah2");
    user.setPassword("password1234");
    user.getDetails().setEmailAddress("josiah@whatever1");

    final User user2 = new User();
    user2.setUsername("Josiah2");
    user2.setPassword("password1234");
    user.getDetails().setEmailAddress("josiah@whatever5");

    User u = service.signup(user).getUser();
    assertThat(u.getPassword(), is(not("password1234")));
    assertThat(u.getUsername(), is("Josiah2"));

    RegistrationRequest singleResult =
        entityManager
            .createQuery(
                "select r from RegistrationRequest r join r.user u where u.id = :id",
                RegistrationRequest.class)
            .setParameter("id", user.getId())
            .getSingleResult();
    authenticationService.authenticate(user2);
    service.approve(singleResult.getRequestId());

    Authentication token = authenticationService.authenticate(user2);
    assertThat(token, is(not(nullValue())));
  }

  @Test
  @Rollback
  public void ensureAuthenticatingWithBadPasswordFails() {
    assertThrows(
        InvalidCredentialException.class,
        () -> {
          final User user = new User();
          user.setUsername("Josiah3");
          user.setPassword("password1");
          user.getDetails().setEmailAddress("josiah@whatever");
          service.signup(user);
          User fake = new User();
          fake.setUsername("Josiah3");
          fake.setPassword("password");
          user.getDetails().setEmailAddress("josiah@whatever2");
          authenticationService.authenticate(fake);
        });
  }

  @Test
  @Rollback
  public void ensureCreatingTokenWorks() {
    final User user = new User();
    user.setUsername("Josiah4");
    user.getDetails().setEmailAddress("josiah@whatever3");
    user.setPassword("password1");
    final User signedup = service.signup(user).getUser();

    long t1 = System.currentTimeMillis();
    String token = encryptionService.createToken(signedup);
    User found = encryptionService.findByToken(token);
    assertThat(found, is(not(nullValue())));
    long t2 = System.currentTimeMillis();

    System.out.format("Time per operation: %s\n", ((float) (t2 - t1)) / 1000f);
  }
}
