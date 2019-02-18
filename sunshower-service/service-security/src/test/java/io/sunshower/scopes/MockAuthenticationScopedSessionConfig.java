package io.sunshower.scopes;

import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.conversation.Conversation;
import io.sunshower.scopes.conversation.ConversationScope;
import io.sunshower.scopes.conversation.ThreadScopedConversation;
import io.sunshower.scopes.security.AuthenticationScope;
import io.sunshower.service.NamedLazyObjectProvider;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.mockito.Mockito.mock;

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
  public Session userFacade() {
    return session;
  }

  @Bean
  @Scope("authentication")
  public String authenticationScopedBean() {
    return session.getUsername() + called++;
  }

  @Bean
  public KeyProvider keyProvider() {
    return new InstanceSecureKeyGenerator(mock(EncryptionService.class));
  }

  @Bean
  public CacheManager cacheManager() {
    val cmanager = new SimpleCacheManager();
    cmanager.setCaches(
        Arrays.asList(
            new ConcurrentMapCache("caches:spring:authentication"),
            new ConcurrentMapCache("caches:spring:conversation")));
    return cmanager;
  }

  @Bean
  @Scope("conversation")
  public String conversationScopedBean(Conversation conversation) {
    return String.format("conversation:" + conversation.getId());
  }

  @Bean(name = "caches:spring:authentication")
  public Cache authenticationCache(CacheManager cacheManager) {
    return cacheManager.getCache("caches:spring:authentication");
  }

  @Bean(name = "caches:spring:conversation")
  public Cache conversationCache(CacheManager cacheManager) {
    return cacheManager.getCache("caches:spring:conversation");
  }

  @Bean
  public Conversation threadScopedConversation() {
    return new ThreadScopedConversation();
  }

  @Bean
  public CustomScopeConfigurer sessionScopeConfiguration(ApplicationContext context) {
    val configurer = new CustomScopeConfigurer();
    val keyProvider =
        new NamedLazyObjectProvider<KeyProvider>("keyProvider", KeyProvider.class, context);
    val sessionProvider =
        new NamedLazyObjectProvider<Session>("userFacade", Session.class, context);
    val authCacheProvider =
        new NamedLazyObjectProvider<Cache>("caches:spring:authentication", Cache.class, context);
    val conversationCacheProvider =
        new NamedLazyObjectProvider<Cache>("caches:spring:conversation", Cache.class, context);
    val conversationProvider =
        new NamedLazyObjectProvider<>("threadScopedConversation", Conversation.class, context);
    configurer.addScope(
        "authentication", new AuthenticationScope(authCacheProvider, sessionProvider, keyProvider));
    configurer.addScope(
        "conversation",
        new ConversationScope(
            conversationProvider, conversationCacheProvider, sessionProvider, keyProvider));
    return configurer;
  }
}
