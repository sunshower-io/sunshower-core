package io.sunshower.scopes.security;

import static org.mockito.Mockito.spy;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.service.security.Session;
import io.sunshower.service.security.crypto.InstanceSecureKeyGenerator;
import java.util.Arrays;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableCaching
class MockAuthenticationScopedSessionConfig {

  private final Session session;

  @Getter private AuthenticationScope scope;

  public MockAuthenticationScopedSessionConfig(Session session) {
    this.session = session;
  }

  @Bean
  public Session session() {
    return session;
  }

  @Bean
  @Scope("authentication")
  public String authenticationScopedBean() {
    return session.getUsername();
  }

  @Bean
  public KeyProvider keyProvider() {
    return new InstanceSecureKeyGenerator();
  }

  @Bean
  public CacheManager cacheManager() {
    val cmanager = new SimpleCacheManager();
    cmanager.setCaches(Arrays.asList(new ConcurrentMapCache("authentication-scope")));
    return cmanager;
  }

  @Bean
  public Cache cache(CacheManager cacheManager) {
    return cacheManager.getCache("authentication-scope");
  }

  @Bean
  public CustomScopeConfigurer sessionScopeConfiguration(
      Cache cache, KeyProvider provider, Session session) {

    val configurer = new CustomScopeConfigurer();
    configurer.addScope(
        "authentication", (scope = spy(new AuthenticationScope(cache, session, provider))));
    return configurer;
  }
}
