package io.sunshower.scopes.security;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.AbstractSessionAwareScope;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;

@Slf4j
public class AuthenticationScope extends AbstractSessionAwareScope
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {

  public AuthenticationScope(Cache cache, Session session, KeyProvider keyProvider) {
    super(cache, session, keyProvider);
  }
}
