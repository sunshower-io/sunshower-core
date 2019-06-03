package io.sunshower.scopes;

import com.google.common.cache.CacheBuilder;
import io.sunshower.model.core.auth.UserConfigurations;
import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;

@Slf4j
public abstract class AbstractDynamicScope<K extends Serializable>
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {
  protected final Provider<Cache> cacheProvider;
  protected final Provider<Session> sessionProvider;
  private final Map<String, Runnable> destructionCallbacks;

  protected final Provider<KeyProvider> keyProvider;

  protected AbstractDynamicScope(
      Provider<Cache> cacheProvider, Provider<Session> session, Provider<KeyProvider> keyProvider) {
    this.sessionProvider = session;
    this.keyProvider = keyProvider;
    this.cacheProvider = cacheProvider;
    destructionCallbacks = new HashMap<>();
  }

  protected KeyProvider keyProvider() {
    return keyProvider.get();
  }

  protected Cache cache() {
    return cacheProvider.get();
  }

  protected Session session() {
    return sessionProvider.get();
  }

  protected abstract int getMaxSize();

  @Override
  @SuppressWarnings("unchecked")
  public Object get(String name, ObjectFactory<?> objectFactory) {
    val id = getId();
    val region = resolveRegion(id);
    var scopedObject = region.get(name);

    if (scopedObject == null) {
      scopedObject = objectFactory.getObject();
      region.put(name, scopedObject);
    }
    return scopedObject;
  }

  @Override
  public Object remove(String name) {
    return resolveRegion(getId()).remove(name);
  }

  @Override
  public void registerDestructionCallback(String name, Runnable callback) {
    destructionCallbacks.put(name, callback);
  }

  @Override
  public Object resolveContextualObject(String key) {
    return null;
  }

  @Override
  public String getConversationId() {
    return cacheKey(session());
  }

  @Override
  public void destroy() {

    log.info("Clearing all existing authentication-scoped data");
    clearSessions();
    log.info("Successfully cleared all authentication-scoped data");

    log.info("Shutting down AuthenticationScope");
    for (Runnable callback : destructionCallbacks.values()) {
      try {
        log.info("Running destruction callback: {}", callback);
        callback.run();
      } catch (Exception ex) {
        log.warn(
            "Error: Caught exception {} while executing destruction callback: {}",
            ex.getMessage(),
            callback);
      }
    }
    log.info("Authentication callbacks run");
  }

  @Override
  public void onApplicationEvent(LogoutEvent event) {
    clearSessions();
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> resolveRegion(K id) {
    if (id == null) {
      throw new NoActiveConversationException("No conversation active!");
    }
    val nodeId = cacheKey(session());
    val cache = cache();
    var scope =
        (Map<K, com.google.common.cache.Cache<String, Object>>) cache.get(nodeId, Map.class);

    if (scope == null) {
      scope = new ConcurrentHashMap<>();
    }

    val timeout = getTimeoutMillis();
    final com.google.common.cache.Cache<String, Object> userCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
            .maximumSize(getMaxSize())
            .build();
    val result = scope.computeIfAbsent(id, i -> userCache).asMap();
    cache.putIfAbsent(nodeId, scope);
    return result;
  }

  private void clearSessions() {
    cache().evict(cacheKey(session()));
  }

  protected long getTimeoutMillis() {
    val cfg = session().getUserConfiguration();
    Integer timeout = cfg.getValue(UserConfigurations.Keys.Timeout);
    if (timeout == null) {
      return TimeUnit.MINUTES.toMillis(60);
    }
    return timeout;
  }

  protected abstract K getId();

  protected abstract String cacheKey(Session session);
}
