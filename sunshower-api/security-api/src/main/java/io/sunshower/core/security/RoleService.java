package io.sunshower.core.security;

import io.sunshower.model.core.auth.Role;
import java.util.Collection;
import java.util.List;

public interface RoleService {

  Role findOrCreate(Role role);

  List<Role> findOrCreate(Collection<Role> roles);
}
