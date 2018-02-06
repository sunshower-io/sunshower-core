package io.sunshower.service.security;

import io.sunshower.model.core.auth.User;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Impersonation implements Authentication, UserDetails {
  final GrantedAuthority[] roles;

  public Impersonation(GrantedAuthority... roles) {
    this.roles = roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.unmodifiableList(Arrays.asList(roles));
  }

  @Override
  public String getPassword() {
    return "impersonation";
  }

  @Override
  public String getUsername() {
    return "impersonation";
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
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
