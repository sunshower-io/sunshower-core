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

import lombok.val;
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
    val username = user.getUsername();
    val password = user.getPassword();
    try {
      val u = userService.findByUsername(username);
      if (encryptionService.matches(password, u.getPassword())) {
        if (!u.isEnabled()) {
          throw new InvalidCredentialException(
              "Account has not been activated yet.  "
                  + "If you believe "
                  + "this is in error, please contact your system administrator");
        }
        val token = encryptionService.createToken(u);
        val details = u.getDetails();
        details.setLastActive(new Date());
        details.setLoginCount(details.getLoginCount() + 1);
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
    val user = encryptionService.findByToken(token.getToken());
    return new Authentication(user, token);
  }
}
