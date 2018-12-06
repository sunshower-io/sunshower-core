package io.sunshower.service;

import javax.inject.Provider;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class NamedLazyObjectProvider<T> implements Provider<T> {
  final String name;
  final Class<T> type;
  final ApplicationContext context;

  @Override
  public T get() {
    return context.getBean(name, type);
  }
}
