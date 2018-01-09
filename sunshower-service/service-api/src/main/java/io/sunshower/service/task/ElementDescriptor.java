package io.sunshower.service.task;

import io.sunshower.common.Identifier;

/** Created by haswell on 3/26/17. */
public class ElementDescriptor<T> {

  private T instance;
  private final String name;
  private final Identifier id;
  private final Object element;
  private final Class<T> type;

  public ElementDescriptor(Object element, Class<T> type, String name, Identifier id) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.element = element;
  }

  public String getName() {
    return name;
  }

  public Identifier getId() {
    return id;
  }

  public Object getElement() {
    return element;
  }

  public T getInstance() {
    return instance;
  }

  public void setInstance(T instance) {
    this.instance = instance;
  }

  public Class<T> getType() {
    return type;
  }
}
