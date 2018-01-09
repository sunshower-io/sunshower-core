package io.sunshower.service.security;

import io.sunshower.core.security.InvalidTokenException;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.User;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/** Created by haswell on 3/5/17. */
public class GridTokenManager implements TokenManager {
  static final Logger log = Logger.getLogger(GridTokenManager.class.getName());

  @Inject private EncryptionService encryptionService;

  @Override
  public void checkEncoded(String token) {
    try {
      check(URLDecoder.decode(token, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void check(String token) {
    if (token != null) {
      try {
        final User user = encryptionService.findByToken(token);
        if (user != null) {
          SecurityContextHolder.getContext()
              .setAuthentication(
                  new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        } else {
          throw new BadCredentialsException("No Valid authentication present");
        }
      } catch (InvalidTokenException ex) {
        log.info("No valid authentication information");
      }
    }
  }
}
