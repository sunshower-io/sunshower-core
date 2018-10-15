package io.sunshower.service.ext;

import static io.sunshower.service.ext.BaseUserServiceTest.testUser;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.UserService;
import io.sunshower.model.core.Image;
import io.sunshower.model.core.auth.Details;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.AuthenticatedTestCase;
import io.sunshower.service.security.PermissionsService;
import javax.inject.Inject;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.test.context.support.WithUserDetails;

class DefaultIconServiceTest extends AuthenticatedTestCase {

  @Inject private UserService userService;
  @Inject private IconService iconService;
  @Inject private RoleService roleService;
  @Inject private PermissionsService<Permission> permissionsService;

  @Test
  void ensureWhateverWorks() {
    val img = new String(iconService.iconDirect("Josiah", 64, 64).getData());
    val e =
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"64\" height=\"64\" viewBox=\"0 0 64 64\" preserveAspectRatio=\"xMidYMid meet\"><path fill=\"#2e8c68\" d=\"M16 16L16 0L24 0ZM32 0L48 0L48 8ZM48 48L48 64L40 64ZM32 64L16 64L16 56ZM0 32L0 16L8 16ZM48 16L64 16L64 24ZM64 32L64 48L56 48ZM16 48L0 48L0 40Z\"/><path fill=\"#59c79d\" d=\"M2 8a5,5 0 1,0 10,0a5,5 0 1,0 -10,0M50 8a5,5 0 1,0 10,0a5,5 0 1,0 -10,0M50 56a5,5 0 1,0 10,0a5,5 0 1,0 -10,0M2 55a5,5 0 1,0 10,0a5,5 0 1,0 -10,0M16 16L32 16L32 18L25 32L16 32ZM48 16L48 32L45 32L32 25L32 16ZM48 48L32 48L32 45L38 32L48 32ZM16 48L16 32L18 32L32 38L32 48Z\"/></svg>";
    System.out.println(img);
    assertThat(img, is(e));
  }

  @Test
  @WithUserDetails("user")
  void ensureChangingImageWorks() {
    final User user = testUser(roleService);
    permissionsService.impersonate(
        () -> {
          userService.save(user);
        },
        "administrator");

    permissionsService.impersonate(
        () -> {
          val img = iconService.iconDirect("Josiah", 64, 64);
          iconService.setIcon(
              Details.class, userService.findByUsername("test").getDetails().getId(), img);
          val actual =
              new String(userService.findByUsername("test").getDetails().getImage().getData());
          val expected = new String(img.getData());
          assertThat(actual, is(expected));
        },
        "test");
  }

  @Test
  void ensureHashingSimpleObjectWorks() {
    final Image image = iconService.iconFor(new Object(), 64, 64);
    System.out.println(new String(image.getData()));
  }

  @Configuration
  public static class Ctx {
    @Bean
    public IconService iconService() {
      return new IdenticonIconService();
    }
  }
}
