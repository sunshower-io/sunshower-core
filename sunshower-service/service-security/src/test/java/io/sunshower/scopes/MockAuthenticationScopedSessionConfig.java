package io.sunshower.scopes;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.conversation.Conversation;
import io.sunshower.scopes.conversation.ConversationScope;
import io.sunshower.scopes.conversation.ThreadScopedConversation;
import io.sunshower.scopes.security.AuthenticationScope;
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
public class MockAuthenticationScopedSessionConfig {

  public static int called = 0;

  private final Session session;

  @Getter private AuthenticationScope authenticationScope;
  @Getter private ConversationScope conversationScope;

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
    return session.getUsername() + called++;
  }

  @Bean
  public KeyProvider keyProvider() {
    return new InstanceSecureKeyGenerator();
  }

  @Bean
  public CacheManager cacheManager() {
    val cmanager = new SimpleCacheManager();
    cmanager.setCaches(Arrays.asList(new ConcurrentMapCache("authentication-authenticationScope")));
    return cmanager;
  }

  @Bean
  @Scope("conversation")
  public String conversationScopedBean(Conversation conversation) {
    return String.format("conversation:" + conversation.getId());
  }

  @Bean
  public Conversation conversationManager() {
    return new ThreadScopedConversation();
  }

  @Bean("conversation-scope")
  public ConversationScope conversationScope(
      Conversation conversation, Cache cache, KeyProvider provider, Session session) {
    return (conversationScope = new ConversationScope(conversation, cache, session, provider));
  }

  @Bean("authentication-scope")
  public AuthenticationScope authenticationScope(
      Cache cache, KeyProvider provider, Session session) {
    return (authenticationScope = new AuthenticationScope(cache, session, provider));
  }

  @Bean
  public Cache cache(CacheManager cacheManager) {
    return cacheManager.getCache("authentication-authenticationScope");
  }

  @Bean
  public CustomScopeConfigurer sessionScopeConfiguration(
      AuthenticationScope authenticationScope, ConversationScope conversationScope) {

    val configurer = new CustomScopeConfigurer();
    configurer.addScope("authentication", authenticationScope);
    configurer.addScope("conversation", conversationScope);
    return configurer;
  }
}
