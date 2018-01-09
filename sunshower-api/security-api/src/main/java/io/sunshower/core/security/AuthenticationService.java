package io.sunshower.core.security;

import io.sunshower.model.core.auth.Authentication;
import io.sunshower.model.core.auth.Token;
import io.sunshower.model.core.auth.User;
import javax.ws.rs.*;

/** Created by haswell on 10/12/16. */
public interface AuthenticationService {

  Authentication validate(Token token);

  Authentication authenticate(User user);
}
