package io.sunshower.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Created by haswell on 3/5/17. */
public class Injector<T extends Annotation> {

  private final Class<T> scanType;
  protected final NameResolver resolver;
  private final Map<Class<?>, Object> bindings;
  private final Map<InjectionSite, Object> sites;

  public Injector(Class<T> scanType, NameResolver resolver) {
    this.resolver = resolver;
    this.scanType = scanType;
    this.sites = new HashMap<>();
    this.bindings = new HashMap<>();
  }

  public void register(Class<?> boundType, String name, Object instance) {
    this.sites.put(new InjectionSite(boundType, name), instance);
  }

  public void register(Class<?> boundType, Object instance) {
    this.bindings.put(boundType, instance);
  }

  public <U> U inject(Class<U> target, U instance) {
    Objects.requireNonNull(instance);
    Objects.requireNonNull(target);
    for (Class<?> current = target; current != Object.class; current = current.getSuperclass()) {
      doInject(current, target, instance);
    }
    return instance;
  }

  private <U> void doInject(Class<?> current, Class<U> target, U instance) {
    for (Field field : target.getDeclaredFields()) {
      if (field.isAnnotationPresent(scanType)) {
        Object binding = resolveBinding(field);
        if (binding != null) {
          field.setAccessible(true);
          try {
            field.set(instance, binding);
          } catch (IllegalAccessException e) {
            throw new InjectionException(current, target, instance, field, binding, e);
          }
        }
      }
    }
  }

  protected Object resolveBinding(Field field) {
    final String name = resolver.resolve(field);
    final Class<?> ftype = field.getType();
    Object o = sites.get(new InjectionSite(ftype, name));
    if (o != null) {
      return o;
    }
    return bindings.get(ftype);
  }

  static final class InjectionSite {
    final Class<?> type;
    final String name;

    InjectionSite(Class<?> type, String name) {
      this.type = type;
      this.name = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      InjectionSite that = (InjectionSite) o;

      if (type != null ? !type.equals(that.type) : that.type != null) return false;
      return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
      int result = type != null ? type.hashCode() : 0;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      return result;
    }
  }
}
