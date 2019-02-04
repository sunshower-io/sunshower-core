package io.sunshower.security.events;

import io.sunshower.model.core.auth.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LogoutEvent extends ApplicationEvent {

  final User user;

  /**
   * Create a new ApplicationEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public LogoutEvent(final User user, Object source) {
    super(source);
    this.user = user;
  }
}
