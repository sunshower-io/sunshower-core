package io.sunshower.service.security;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Service;

@Service
@Provider
public interface AuthenticationFilter extends ContainerResponseFilter, ContainerRequestFilter {}
