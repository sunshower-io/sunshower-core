package io.sunshower.service.serialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ext.Providers;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

public class DynamicResolvingMoxyJsonProvider extends MOXyJsonProvider {

  private final Map<Class<?>, Set<Class<?>>> registrations;

  public DynamicResolvingMoxyJsonProvider(Providers providers) {
    this.providers = providers;
    this.registrations = new HashMap<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Set<Class<?>> getDomainClasses(Type genericType) {
    if (genericType instanceof Class && registrations.containsKey(genericType)) {
      final Set<Class<?>> classes = new HashSet(registrations.get(genericType));
      classes.add((Class<?>) genericType);
      classes.addAll(super.getDomainClasses(genericType));
      return classes;
    }
    return super.getDomainClasses(genericType);
  }

  public <T> void register(Class<?> graphClass, Class<T> type) {
    Set<Class<?>> classes = registrations.computeIfAbsent(graphClass, k -> new HashSet<>());
    classes.add(type);
  }
}
