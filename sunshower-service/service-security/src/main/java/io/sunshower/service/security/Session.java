package io.sunshower.service.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

import io.sunshower.model.core.auth.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public interface Session extends UserDetails, Authentication {


  Locale getLocale();

  Configuration getUserConfiguration();

  @SuppressWarnings("unchecked")
  <T extends Serializable> T getId();

  @Override
  Collection<? extends GrantedAuthority> getAuthorities();

  @Override
  Object getCredentials();

  @Override
  Object getDetails();

  @Override
  Object getPrincipal();

  @Override
  boolean isAuthenticated();

  @Override
  void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;

  @Override
  String getPassword();

  @Override
  String getUsername();

  @Override
  boolean isAccountNonExpired();

  @Override
  boolean isAccountNonLocked();

  @Override
  boolean isCredentialsNonExpired();

  @Override
  boolean isEnabled();

  @SuppressWarnings("unchecked")
  <T> T unwrap(Class<T> type);

  @Override
  String getName();
}
