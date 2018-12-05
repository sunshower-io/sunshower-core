package io.sunshower.service.security;

import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Provider
public class TokenAuthenticationFilter implements AuthenticationFilter {

    static final Logger log = Logger.getLogger(TokenAuthenticationFilter.class.getName());

    public static final String HEADER_KEY = "X-AUTH-TOKEN";

    @Inject
    private TokenManager tokenManager;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String token = requestContext.getHeaderString(HEADER_KEY);
        tokenManager.check(token);
        SessionLocales.setLocales(requestContext.getAcceptableLanguages());
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
