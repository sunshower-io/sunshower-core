package io.sunshower.service.security;

import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Service;

/** Created by haswell on 10/20/16. */
@Service
@Provider
public class TokenAuthenticationFilter implements ContainerResponseFilter, ContainerRequestFilter {

  static final Logger log = Logger.getLogger(TokenAuthenticationFilter.class.getName());

  public static final String HEADER_KEY = "X-AUTH-TOKEN";

  @Inject private TokenManager tokenManager;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    final String token = requestContext.getHeaderString(HEADER_KEY);
    tokenManager.check(token);
  }

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {}
}
