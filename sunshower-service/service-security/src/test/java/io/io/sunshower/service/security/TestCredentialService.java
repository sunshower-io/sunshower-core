package io.io.sunshower.service.security;

import io.sunshower.core.security.CredentialService;
import io.sunshower.model.core.auth.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/** Created by haswell on 10/17/16. */
public class TestCredentialService implements CredentialService {

  //    @Override
  //    public User authenticate() {
  //        return null;
  //    }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User u = new User();
    u.setPassword("frap");
    u.setUsername(username);
    return u;
  }
}
