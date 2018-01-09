package io.sunshower.core.security;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;

/** Created by haswell on 10/15/16. */
@Path("security")
@Produces(MediaType.APPLICATION_JSON)
public interface CredentialService extends UserDetailsService {}
