package io.sunshower.service.ext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.UserService;
import io.sunshower.model.core.auth.Details;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.AuthenticatedTestCase;
import io.sunshower.service.security.PermissionsService;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.test.context.support.WithUserDetails;

public class BaseUserServiceTest extends AuthenticatedTestCase {
  @Inject private UserService userService;
  @Inject private IconService iconService;
  @Inject private RoleService roleService;
  @Inject private PermissionsService<Permission> permissionsService;

  @Test
  @WithUserDetails("administrator")
  void ensureCreatingUserGrantsThatUserRights() {
    User user = testUser(roleService);
    userService.save(user);
    permissionsService.impersonate(
        () -> {
          final Details details = new Details();
          details.setEmailAddress("hello@world.com");
          details.setImage(iconService.iconDirect("test", 64, 64));
          userService.updateDetails(user.getId(), details);
        },
        "test");

    assertThat(userService.findByUsername("test").getDetails().getImage(), is(not(nullValue())));
    assertThat(
        userService.findByUsername("test").getDetails().getEmailAddress(), is("hello@world.com"));
  }

  @Test
  @WithUserDetails("administrator")
  void ensureUpdatingUserWithoutPermissionsFails() {
    User user = testUser(roleService);
    userService.save(user);
    permissionsService.impersonate(
        () -> {
          final Details details = new Details();
          details.setEmailAddress("nope@world.com");
          details.setImage(iconService.iconDirect("test", 64, 64));
          assertThrows(
              AccessDeniedException.class,
              () -> {
                userService.updateDetails(user.getId(), details);
              });
        },
        "user");
  }

  static User testUser(RoleService roleService) {
    final User user = new User();
    user.setActive(true);
    user.setUsername("test");
    user.setPassword("password");
    user.getDetails().setEmailAddress("test@test.com");
    final Role orCreate = roleService.findOrCreate(new Role("tenant:user"));
    user.addRole(orCreate);
    return user;
  }
}
