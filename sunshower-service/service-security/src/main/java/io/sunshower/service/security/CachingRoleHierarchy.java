package io.sunshower.service.security;

import io.sunshower.model.core.auth.Role;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;

/** Created by haswell on 5/9/17. */
public class CachingRoleHierarchy implements RoleHierarchy {

  @Override
  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      Collection<? extends GrantedAuthority> authorities) {

    return authorities == null
        ? Collections.emptyList()
        : authorities.stream().flatMap(CachingRoleHierarchy::collect).collect(Collectors.toList());
  }

  static Stream<? extends GrantedAuthority> collect(GrantedAuthority authority) {
    if (authority == null) {
      return Stream.empty();
    }
    if (authority instanceof Role) {
      return Stream.concat(
          Stream.of(authority),
          children(((Role) authority).getChildren())
              .stream()
              .flatMap(CachingRoleHierarchy::collect));
    }

    return Stream.of(authority);
  }

  private static Collection<Role> children(Set<Role> children) {
    return children == null ? Collections.emptySet() : children;
  }
}
