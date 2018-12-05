package io.sunshower.scopes;

import com.google.common.cache.CacheBuilder;
import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.UserConfigurations;
import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

@Slf4j
public abstract class AbstractDynamicScope<K extends Serializable>
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {
  private final Cache cache;
  protected final Session session;
  private final Map<String, Runnable> destructionCallbacks;

  protected final KeyProvider keyProvider;

  protected AbstractDynamicScope(Cache cache, Session session, KeyProvider keyProvider) {
    this.cache = cache;
    this.session = session;
    this.keyProvider = keyProvider;
    destructionCallbacks = new HashMap<>();
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
    return cacheKey(session);
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
    val nodeId = cacheKey(session);
    var scope =
        (Map<K, com.google.common.cache.Cache<String, Object>>)
            cache.get(nodeId, Map.class);

    if (scope == null) {
      scope = new ConcurrentHashMap<>();
      cache.put(nodeId, scope);
    }
    val timeout = getTimeoutMillis();
    final com.google.common.cache.Cache<String, Object> userCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
            .maximumSize(getMaxSize())
            .build();
    return scope.computeIfAbsent(id, i -> userCache).asMap();
  }

  private void clearSessions() {
    cache.evict(cacheKey(session));
  }

  public long getTimeoutMillis() {
    val cfg = session.getUserConfiguration();
    Integer timeout = cfg.getValue(UserConfigurations.Keys.Timeout);
    if (timeout == null) {
      return TimeUnit.MINUTES.toMillis(60);
    }
    return timeout;
  }

  protected String cacheKey(Session session) {
    return String.format("%s:%s", keyProvider.getKey(), "authentication-scope");
  }

  protected abstract K getId();


}
