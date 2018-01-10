package io.sunshower.service.security;

import io.sunshower.model.core.auth.ProtectedDistributableEntity;
import io.sunshower.model.core.auth.Role;
import io.sunshower.persistence.core.Persistable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.GrantedAuthority;

import java.util.function.Function;
import java.util.function.Supplier;

public interface PermissionsService<U> {
    
    void impersonate(Action action, GrantedAuthority...roles);

  /**
   * @param type
   * @param instance
   * @param permissions
   * @param <T>
   */
  <T extends Persistable> void grantWithCurrentSession(Class<T> type, T instance, U... permissions);

  /**
   * @param instance
   * @param <T>
   */
  <T extends ProtectedDistributableEntity> void checkPermission(
      T instance, Permission... permission);

  /**
   * @param type
   * @param instance
   * @param permissions
   * @param <T>
   */
  @SuppressWarnings("all")
  <T extends Persistable> void revokeOnCurrentSession(Class<T> type, T instance, U... permissions);

  /**
   * @param type
   * @param instance
   * @param <T>
   */
  <T extends Persistable> void delete(Class<T> type, T instance);

}
