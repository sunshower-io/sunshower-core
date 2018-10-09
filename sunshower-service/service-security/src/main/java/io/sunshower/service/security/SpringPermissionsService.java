package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.*;
import io.sunshower.persistence.core.Persistable;
import java.util.Collections;
import java.util.Objects;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class SpringPermissionsService implements PermissionsService<Permission> {

  @Inject private AuthenticationSession session;

  @Inject private AclService aclService;

  @PersistenceContext private EntityManager entityManager;

  @Inject private PermissionEvaluator permissionEvaluator;

  public void impersonate(Action action, GrantedAuthority... roles) {
    log.info("Anonymous impersonation");
    final Authentication impersonatedAuthentication = new Impersonation(roles);
    try {
      SecurityContextHolder.getContext().setAuthentication(impersonatedAuthentication);
      action.apply();
    } finally {
      SecurityContextHolder.getContext().setAuthentication(session);
    }
  }

  @Override
  public void impersonate(Action action, String username) {
    final User user =
        entityManager
            .createQuery("select u from User u where u.username = :username", User.class)
            .setParameter("username", username)
            .getSingleResult();
    final Authentication impersonation =
        new RunAsUserToken(
            user.getUsername(),
            user.getUsername(),
            user.getPassword(),
            user.getAuthorities(),
            Impersonation.class);
    log.info("Impersonating {}", user.getUsername());
    try {
      SecurityContextHolder.getContext().setAuthentication(impersonation);
      action.apply();
    } finally {
      SecurityContextHolder.getContext().setAuthentication(session);
    }
  }

  @Override
  public void impersonate(Action action, Identifier id) {
    final User user = entityManager.find(User.class, id);
    if (user == null) {
      throw new NotFoundException("User not found");
    }
    final Authentication impersonation =
        new RunAsUserToken(
            id.toString(),
            user.getUsername(),
            user.getPassword(),
            user.getAuthorities(),
            Impersonation.class);
    log.info("Impersonating {}", user.getUsername());
    try {
      SecurityContextHolder.getContext().setAuthentication(impersonation);
      action.apply();
    } finally {
      SecurityContextHolder.getContext().setAuthentication(session);
    }
  }

  @Override
  public <T extends ProtectedDistributableEntity, V extends ProtectedDistributableEntity>
      void grantWithCurrentSession(
          Class<T> type, T instance, Class<V> parent, V parentInstance, Permission... permissions) {

    final ObjectIdentity childOid = new ObjectIdentityImpl(type, instance.getIdentifier());
    final ObjectIdentity parentOid = new ObjectIdentityImpl(parent, parentInstance.getIdentifier());
    final Sid sid = new PrincipalSid(session.getName());

    MutableAcl parentAcl = resolveAcl(parentOid, sid);
    MutableAcl childAcl = resolveAcl(childOid, sid);
    childAcl.setEntriesInheriting(true);
    childAcl.setParent(parentAcl);

    for (Permission permission : permissions) {
      childAcl.insertAce(childAcl.getEntries().size(), permission, sid, true);
    }
    ((MutableAclService) aclService).updateAcl(childAcl);
  }

  @Override
  public <T extends Persistable> void grantWithCurrentSession(
      Class<T> type, T instance, Permission... permissions) {
    final ObjectIdentity oid = new ObjectIdentityImpl(type, instance.getId());
    Sid sid = new PrincipalSid(session.getUsername());
    final MutableAcl acl = resolveAcl(oid, sid);
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

  private MutableAcl resolveAcl(ObjectIdentity parentOid, Sid sid) {
    MutableAcl parentAcl;
    try {
      parentAcl = (MutableAcl) aclService.readAclById(parentOid, Collections.singletonList(sid));
    } catch (NotFoundException ex) {
      parentAcl = ((MutableAclService) aclService).createAcl(parentOid);
    }
    return parentAcl;
  }
}
