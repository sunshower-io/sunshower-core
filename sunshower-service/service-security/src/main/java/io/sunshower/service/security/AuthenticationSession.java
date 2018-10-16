package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Details;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.Persistable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import lombok.val;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationSession implements Session {

  @Override
  public Locale getLocale() {
    List<Locale> locales = SessionLocales.getLocales();
    if (!(locales == null || locales.isEmpty())) {
      final Locale locale = locales.get(0);
      if (locale != null) {
        return locale;
      }
    }

    Object principal = getPrincipal();
    if (principal != null && principal instanceof User) {
      final Details details = ((User) principal).getDetails();
      if (details != null && details.getLocale() != null) {
        return details.getLocale();
      }
    }
    return Locale.getDefault();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Serializable> T getId() {
    Object principal = getPrincipal();
    if (principal != null && principal instanceof User) {
      return ((Persistable<T>) principal).getId();
    }
    return (T) Identifier.random();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return unwrap(UserDetails.class).getAuthorities();
  }

  @Override
  public Object getCredentials() {
    return unwrap(Authentication.class).getCredentials();
  }

  @Override
  public Object getDetails() {
    return unwrap(Authentication.class).getDetails();
  }

  @Override
  public Object getPrincipal() {
    final Authentication result = unwrap(Authentication.class);
    if (result == null) {
      throw new AuthenticationCredentialsNotFoundException("No session");
    }
    return result.getPrincipal();
  }

  @Override
  public boolean isAuthenticated() {
    return unwrap(Authentication.class).isAuthenticated();
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    unwrap(Authentication.class).setAuthenticated(isAuthenticated);
  }

  @Override
  public String getPassword() {
    return unwrap(UserDetails.class).getPassword();
  }

  @Override
  public String getUsername() {
    Object o = unwrap(UserDetails.class);
    if (o instanceof String) {
      return (String) o;
    }
    return ((UserDetails) o).getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return unwrap(UserDetails.class).isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return unwrap(UserDetails.class).isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return unwrap(UserDetails.class).isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return unwrap(UserDetails.class).isEnabled();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> type) {
    if (Authentication.class.isAssignableFrom(type)) {
      return (T) SecurityContextHolder.getContext().getAuthentication();
    }
    if (UserDetails.class.isAssignableFrom(type)) {
      val unwrapped = unwrap(Authentication.class);
      if (unwrapped == null) {
        throw new IllegalArgumentException("Cannot unwrap " + type + " ");
      }
      return (T) unwrapped.getPrincipal();
    }
    return null;
  }

  @Override
  public String getName() {
    return null;
  }
}
