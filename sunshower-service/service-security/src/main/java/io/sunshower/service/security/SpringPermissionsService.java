package io.sunshower.service.security;

import io.sunshower.model.core.auth.*;
import io.sunshower.persistence.core.Persistable;
import java.util.Objects;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringPermissionsService implements PermissionsService<Permission> {

  @Inject private Session session;

  @Inject private AclService aclService;

  @PersistenceContext private EntityManager entityManager;

  @Inject private PermissionEvaluator permissionEvaluator;

  public void impersonate(Action action, GrantedAuthority... roles) {
    final Authentication impersonatedAuthentication = new Impersonation(roles);
    try {
      SecurityContextHolder.getContext().setAuthentication(impersonatedAuthentication);
      action.apply();
    } finally {
      SecurityContextHolder.getContext().setAuthentication(session);
    }
  }

  @Override
  public <T extends Persistable> void grantWithCurrentSession(
      Class<T> type, T instance, Permission... permissions) {
    final ObjectIdentity oid = new ObjectIdentityImpl(type, instance.getId());
    Sid sid = new PrincipalSid(session.getUsername());
    MutableAcl acl;
    try {
      acl = (MutableAcl) aclService.readAclById(oid);
    } catch (NotFoundException ex) {
      acl = ((MutableAclService) aclService).createAcl(oid);
    }
    for (Permission permission : permissions) {
      acl.insertAce(acl.getEntries().size(), permission, sid, true);
    }
    ((MutableAclService) aclService).updateAcl(acl);
  }

  @Override
  public <T extends ProtectedDistributableEntity> void checkPermission(
      T instance, Permission... permissions) {
    Objects.requireNonNull(instance, "Cannot check permissions on null object");
    org.springframework.security.core.Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();
    for (Permission permission : permissions) {
      boolean b = permissionEvaluator.hasPermission(authentication, instance, permission);
      if (!b) {
        throw new AccessDeniedException(
            String.format(
                "Authentication %s does not have permission %s on object %s",
                authentication, permission, instance));
      }
    }
  }

  @Override
  public <T extends Persistable> void revokeOnCurrentSession(
      Class<T> type, T instance, Permission... permissions) {}

  @Override
  public <T extends Persistable> void delete(Class<T> type, T instance) {
    final ObjectIdentity oid = new ObjectIdentityImpl(type, instance.getId());
    ((MutableAclService) aclService).deleteAcl(oid, true);
  }
}
