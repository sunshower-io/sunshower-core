package io.sunshower.model.core.events;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.ImageAware;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ImageChangedEvent extends ApplicationEvent {
  private final Identifier identifier;
  private final Class<? extends ImageAware> targetType;

  /**
   * Create a new ApplicationEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public ImageChangedEvent(Identifier id, Class<? extends ImageAware> type, Object source) {
    super(source);
    this.identifier = id;
    this.targetType = type;
  }
}
