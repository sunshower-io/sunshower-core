package io.sunshower.service.security;

import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Impersonation extends User implements Authentication, UserDetails {
  final GrantedAuthority[] roles;
  final Tenant tenant = new Tenant();

  public Impersonation(GrantedAuthority... roles) {
    this.roles = roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.unmodifiableList(Arrays.asList(roles));
  }

  @Override
  public Tenant getTenant() {
    return tenant;
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
  public Object getPrincipal() {
    return this;
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
