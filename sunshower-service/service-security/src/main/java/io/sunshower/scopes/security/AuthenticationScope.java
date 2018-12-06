package io.sunshower.scopes.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.AbstractDynamicScope;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

@Slf4j
public class AuthenticationScope extends AbstractDynamicScope<Identifier>
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {

  public AuthenticationScope(Cache cache, Session session, KeyProvider keyProvider) {
    super(cache, session, keyProvider);
  }

  /**
   * TODO: configure off of application instance?
   *
   * @return the maximum number of concurrent users
   */
  @Override
  protected int getMaxSize() {
    return 10000;
  }

  public void destroy() {
    super.destroy();
  }

  protected Identifier getId() {
    final Identifier id = session.getId();
    if (id == null) {
      throw new AuthenticationCredentialsNotFoundException("Nobody appears to be logged in");
    }
    return id;
  }

  protected String cacheKey(Session session) {
    return String.format("%s:%s", keyProvider.getKey(), "authentication-scope");
  }
}
