package io.sunshower.core.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.AbstractProperty;
import io.sunshower.model.core.auth.*;
import java.util.Collection;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserService {

  User get(Identifier id);

  Configuration getConfiguration(Identifier userId);

  User delete(Identifier id);

  void setConfiguration(Identifier userId, Collection<? extends AbstractProperty> properties);

  User findByUsername(String username);

  @PreAuthorize(
      "hasAuthority('admin') || hasPermission(#id, 'io.sunshower.model.core.auth.User', 'WRITE')")
  User updateDetails(Identifier id, Details details);

  @PreAuthorize("hasAuthority('admin')")
  User save(User u);

  @PreAuthorize("hasAuthority('admin')")
  List<User> activeUsers();

  @PreAuthorize("hasAuthority('admin')")
  List<User> inactiveUsers();

  void evict(Identifier id);
}
