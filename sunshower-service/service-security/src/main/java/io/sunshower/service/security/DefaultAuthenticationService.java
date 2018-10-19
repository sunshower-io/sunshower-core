package io.sunshower.service.security;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.core.security.InvalidCredentialException;
import io.sunshower.core.security.UserService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Authentication;
import io.sunshower.model.core.auth.Token;
import io.sunshower.model.core.auth.User;
import java.util.Date;
import javax.inject.Inject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultAuthenticationService implements AuthenticationService {

  @Inject private UserService userService;

  @Inject private EncryptionService encryptionService;

  @Override
  @Transactional(noRollbackFor = InvalidCredentialException.class)
  public Authentication authenticate(User user) {
    final String username = user.getUsername();
    final String password = user.getPassword();
    try {
      final User u = userService.findByUsername(username);
      if (encryptionService.matches(password, u.getPassword())) {
        if (!u.isEnabled()) {
          throw new InvalidCredentialException(
              "Account has not been activated yet.  "
                  + "If you believe "
                  + "this is in error, please contact your system administrator");
        }
        final String token = encryptionService.createToken(u);
        u.getDetails().setLastActive(new Date());
        return new Authentication(u, new Token(token, new Date()));
      }
    } catch (UsernameNotFoundException ex) {
      //      throw new UsernameNotFoundException(user.getUsername());
      throw new InvalidCredentialException(ex);
    }
    //    throw new UsernameNotFoundException(user.getUsername());
    throw new InvalidCredentialException("Username or password combination is invalid");
  }

  @Override
  public Authentication validate(Token token) {
    final User user = encryptionService.findByToken(token.getToken());
    return new Authentication(user, token);
  }
}
