package io.sunshower.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import org.springframework.context.ApplicationContext;

public class DefaultServiceResolver implements ServiceResolver {

  static final String defaultRegion = "default";

  final Map<Object, Map<String, Object>> services;

  @Inject private ApplicationContext applicationContext;

  public DefaultServiceResolver() {
    services = new ConcurrentHashMap<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T resolve(Class<T> type, String key) {
    return resolve(type, defaultRegion, key);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T, U> T resolve(Class<T> type, U region, String key) {
    return (T)
        services
            .computeIfAbsent(region, t -> new ConcurrentHashMap<>())
            .computeIfAbsent(key, t -> applicationContext.getBean(key, type));
  }

  @Override
  public <T, U> boolean register(Class<T> type, U region, String key, T object) {
    return services.computeIfAbsent(region, t -> new ConcurrentHashMap<>()).put(key, object)
        == null;
  }

  @Override
  public <T> boolean register(Class<T> type, String key, T object) {
    return register(type, defaultRegion, key, object);
  }
}
