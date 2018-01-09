package io.io.sunshower.service.security;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.sunshower.common.Identifier;
import io.sunshower.common.crypto.Hashes;
import io.sunshower.common.crypto.Multihash;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.service.signup.RegistrationRequest;
import io.sunshower.service.signup.SignupService;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

public class DefaultSignupServiceTest extends SecurityTest {

  @PersistenceContext private EntityManager entityManager;

  @Inject private SignupService localService;

  @Test
  public void ensureSignupServiceIsInjected() {
    assertThat(localService, is(not(nullValue())));
  }

  @Test
  public void ensureRevokeIsInaccessibleForUnauthenticatedUser() {
    assertThrows(
        AuthenticationCredentialsNotFoundException.class,
        () -> {
          localService.revoke(Identifier.random());
        });
  }

  @Test
  public void ensureApproveIsInaccessibleForUnauthenticatedUser() {
    assertThrows(
        AuthenticationCredentialsNotFoundException.class,
        () -> {
          localService.approve(Hashes.create(Multihash.Type.SHA_2_256).hash(UUID.randomUUID()));
        });
  }

  @Test
  @WithMockUser(username = "frap", password = "cool", authorities = "russian:hacker")
  public void ensureApproveIsInaccessibleForUserWithoutAdminRole() {
    assertThrows(
        AccessDeniedException.class,
        () -> {
          localService.approve(Hashes.create(Multihash.Type.SHA_2_256).hash(UUID.randomUUID()));
        });
  }

  @Test
  public void ensurePendingRegistrationsAreInaccessibleForUserWithoutAdminRole() {
    assertThrows(
        AuthenticationCredentialsNotFoundException.class,
        () -> {
          localService.pendingRegistrations();
        });
  }

  @Test
  @WithMockUser(username = "frap", password = "cool", authorities = "admin")
  public void ensureSavingThenRevokingUserRegeneratesRegistrationRequest() {

    final User user = new User();
    user.setUsername("Josiah1312341234adsfasdfasdf");
    user.setPassword("frap");
    user.getDetails().setEmailAddress("frapadapasdfasdfasdfasdf@gmail.com");
    RegistrationRequest signup = localService.signup(user);
    assertThat(localService.pendingRegistrations().size(), is(1));
    localService.approve(signup.getRequestId());
    assertThat(localService.pendingRegistrations().size(), is(0));
    User revoke = localService.revoke(user.getId());
    assertThat(localService.pendingRegistrations().size(), is(1));
  }

  @Test
  @WithMockUser(username = "frap", password = "cool", authorities = "admin")
  public void ensureSignupServiceCreatesRequestAndInactiveUser() {
    final User user = new User();
    user.setUsername("Josiah1312341234");
    user.setPassword("frap");
    user.getDetails().setEmailAddress("frapadap@gmail.com");
    RegistrationRequest request = localService.signup(user);
    User u = request.getUser();
    assertThat(u.isEnabled(), is(false));
    List<RegistrationRequest> registrationRequests = localService.pendingRegistrations();
    assertThat(registrationRequests.isEmpty(), is(false));
    entityManager.remove(u);
    entityManager.remove(request);
    entityManager.flush();
  }

  @Test
  @WithMockUser(username = "frap", password = "cool", authorities = "admin")
  public void ensureApprovingRegistrationApprovesTheCorrectUserWhenMultipleOptionsAreAvailable() {
    final User fst = create();
    final User snd = create();
    final User third = create();

    RegistrationRequest fstr = localService.signup(fst);
    RegistrationRequest sndr = localService.signup(snd);
    RegistrationRequest thirdr = localService.signup(third);

    localService.approve(fstr.getRequestId());

    assertThat(localService.pendingRegistrations().size(), is(2));
    List<RegistrationRequest> registrationRequests = localService.pendingRegistrations();
    assertThat(registrationRequests.contains(sndr), is(true));
    assertThat(registrationRequests.contains(thirdr), is(true));
  }

  @Test
  @WithMockUser(username = "frap", password = "cool", authorities = "admin")
  public void ensureRevokingUserSetsUserActiveToFalse() {

    final User fst = create();
    RegistrationRequest signup = localService.signup(fst);
    localService.approve(signup.getRequestId());
    localService.revoke(fst.getId());
    assertThat(entityManager.find(User.class, fst.getId()).isActive(), is(false));
  }

  @Test
  @WithMockUser(username = "frap", password = "cool", authorities = "admin")
  public void ensureRegistrationRequestIsClearedWhenUserIsActivated() {

    final User user = new User();
    user.setUsername("Josiah131234123423235");
    user.setPassword("frap");
    user.getDetails().setEmailAddress("fraadfafpadap@gmail.com");
    RegistrationRequest request = localService.signup(user);
    User u = request.getUser();
    List<RegistrationRequest> registrationRequests = localService.pendingRegistrations();
    assertThat(registrationRequests.isEmpty(), is(false));
    localService.approve(request.getRequestId());
    assertThat(localService.pendingRegistrations().isEmpty(), is(true));

    assertThat(entityManager.find(User.class, u.getId()).isAccountNonExpired(), is(true));
    assertThat(entityManager.find(User.class, u.getId()).isActive(), is(true));
  }

  @Test
  public void ensureAttemptingToSignUpSameUsernameResultsInFailure() {
    assertThrows(
        PersistenceException.class,
        () -> {
          final User user = new User();
          user.setUsername("duplicate");
          user.setPassword("password");
          user.getDetails().setEmailAddress("unique@gmail.com");

          final User snd = new User();
          snd.setUsername("duplicate");
          snd.setPassword("password");
          user.getDetails().setEmailAddress("unique2@gmail.com");

          localService.signup(user);
          localService.signup(snd);
        });
  }

  final User create() {
    return create(
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
  }

  final User create(String username, String password, String email) {

    final User user = new User();
    user.setUsername(username);
    user.setPassword(password);
    user.getDetails().setEmailAddress(email);
    return user;
  }
}
