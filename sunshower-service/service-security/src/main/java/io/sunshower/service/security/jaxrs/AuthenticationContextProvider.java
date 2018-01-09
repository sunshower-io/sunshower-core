package io.sunshower.service.security.jaxrs;

import io.sunshower.model.core.auth.User;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.springframework.security.core.context.SecurityContextHolder;

/** Created by haswell on 11/2/16. */
@Provider
public class AuthenticationContextProvider implements ContextResolver<User> {

  @Override
  public User getContext(Class<?> type) {
    return (User) SecurityContextHolder.getContext().getAuthentication();
  }
}
