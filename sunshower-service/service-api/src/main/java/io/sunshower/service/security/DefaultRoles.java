package io.sunshower.service.security;

import io.sunshower.model.core.auth.Role;

/**
 * DefaultRoles
 * 
 * 
 * @author haswell 
 */

public enum DefaultRoles {
  TENANT_USER("tenant:user", "Tenant user"),
  SITE_ADMINISTRATOR("admin", "Global administrator") {
    public Role toRole() {
      final Role role = new Role(super.name, super.description);
      role.addChild(DefaultRoles.TENANT_USER.toRole());
      return role;
    }
  },
  TENANT_ADMINISTRATOR("tenant:admin", "Administrator for entire tenant");

  private final String name;
  private final String description;

  DefaultRoles(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Role toRole() {
    return new Role(name, description);
  }

  public String authority() {
    return name;
  }
}
