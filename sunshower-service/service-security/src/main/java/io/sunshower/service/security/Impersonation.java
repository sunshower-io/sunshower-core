package io.sunshower.service.security;

import io.sunshower.model.core.auth.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Impersonation implements Authentication {
  final GrantedAuthority[] roles;

  public Impersonation(GrantedAuthority... roles) {
    this.roles = roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.unmodifiableList(Arrays.asList(roles));
  }

  @Override
  public Object getCredentials() {
      return new User();
  }

  @Override
  public Object getDetails() {
      return new Object();
  }

  @Override
  public Object getPrincipal() {
      return new Object();
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

  @Override
  public String getName() {
    return "impersonation";
  }
}
