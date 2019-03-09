package io.sunshower.model.core.events;

import io.sunshower.common.Identifier;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CacheEvictionEvent extends ApplicationEvent {
  private final Identifier identifier;
  private final Class<?> targetType;

  /**
   * Create a new ApplicationEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public CacheEvictionEvent(Identifier id, Class<?> type, Object source) {
    super(source);
    this.identifier = id;
    this.targetType = type;
  }
}
