package io.sunshower.service.task.exec;

import io.sunshower.common.Identifier;
import io.sunshower.service.task.ElementDescriptor;
import io.sunshower.service.task.TaskContext;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

/** Created by haswell on 3/27/17. */
class InjectionSite {

  private final String name;
  private final Field field;
  private final Object instance;
  private final String fieldName;
  private final TaskContext context;
  private final ElementDescriptor descriptor;

  public InjectionSite(
      TaskContext context,
      String fieldName,
      String name,
      Field actualField,
      Object instance,
      TinkerGraph graph,
      ElementDescriptor descriptor,
      InjectionContext injectionContext) {
    this.name = name;
    this.context = context;
    this.field = actualField;
    this.instance = instance;
    this.fieldName = fieldName;
    this.descriptor = descriptor;
  }

  public boolean resolveAndInject() {
    try {
      final Class<?> type = field.getType();
      field.setAccessible(true);
      if (name.equals("name") && String.class.equals(type)) {
        field.set(instance, descriptor.getName());
        return true;
      } else if (name.equals("id") && Identifier.class.equals(type)) {
        field.set(instance, descriptor.getId());
        return true;
      } else if (context.containsBinding(type)) {
        field.set(instance, context.getBinding(type));
        return true;
      } else {
        Object element = descriptor.getElement();
        if (element != null) {
          final Class<?> etype = AopUtils.getTargetClass(element);
          if (type.isAssignableFrom(etype)) {
            field.set(instance, element);
            return true;
          }
        }
      }

    } catch (IllegalAccessException e) {
      ParallelTaskExecutor.log.log(Level.WARNING, "Failed to inject site.  Reason: ", e);
    }
    return false;
  }

  public void inject(Map<Class<?>, Object> resolved) {
    final Class<?> type = field.getType();
    try {
      if (resolved.containsKey(type)) {
        final Object value = resolved.get(type);
        ReflectionUtils.setField(field, instance, value);
        return;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    for (Map.Entry<Class<?>, Object> entries : resolved.entrySet()) {
      final Class<?> currentType = entries.getKey();
      if (currentType.isAssignableFrom(type)) {
        ReflectionUtils.setField(field, instance, entries.getValue());
        break;
      }
    }
  }
}
