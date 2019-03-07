package io.sunshower.service.security;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.test.persist.Principal;
import javax.inject.Inject;
import lombok.val;
import org.apache.ignite.IgniteCache;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithUserDetails;

class AuthenticationSessionTest extends SecurityTest {

  @Inject private Session session;
  @Inject private CacheManager cacheManager;

  @Principal
  public User testUser() {
    final User user = new User(Identifier.random(), "josiah", "coolbeans");
    user.getDetails().setEmailAddress("joe@email.com3242adf");
    user.addRole(new Role("frap"));
    user.setActive(true);
    return user;
  }

  @Test
  @WithUserDetails("josiah")
  void ensureDetailsWorks() {
    assertThat(session.getUserConfiguration(), is(not(nullValue())));
  }

  @Test
  void ensureCacheSerializationWorks() {
    IgniteCache<Object, Object> proxy =
        (IgniteCache<Object, Object>) cacheManager.getCache("test").getNativeCache();
    proxy.put("hello", new User());
    val user = proxy.get("hello");
    assertThat(user instanceof User, is(true));
  }
}
