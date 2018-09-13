package io.sunshower.service;

import io.sunshower.model.core.auth.ProtectedDistributableEntity;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.persistence.core.DistributableHierarchicalEntity;
import io.sunshower.persistence.core.Persistable;
import io.sunshower.service.repository.EntityRepository;
import io.sunshower.service.security.PermissionsService;
import io.sunshower.service.security.Session;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class BaseRepository<ID extends Serializable, E extends Persistable<ID>>
    implements EntityRepository<ID, E> {

  private final String entityName;
  private final Class<E> entityType;

  @Inject private Session session;

  @PersistenceContext private EntityManager entityManager;

  @Inject private PermissionsService<Permission> permissionService;

  protected BaseRepository(Class<E> entityType) {
    this.entityType = entityType;
    this.entityName = entityType.getSimpleName();
  }

  protected BaseRepository(Class<E> entityType, final String entityName) {
    this.entityName = entityName;
    this.entityType = entityType;
  }

  @Override
  @PreAuthorize("hasAuthority('tenant:user')")
  public E create(E entity) {
    entityManager.persist(entity);
    permissionService.grantWithCurrentSession(
        entityType,
        entity,
        BasePermission.ADMINISTRATION,
        BasePermission.WRITE,
        BasePermission.READ,
        BasePermission.DELETE);
    entityManager.flush();
    return entity;
  }

  @Override
  @PreAuthorize("hasPermission(#entity, 'WRITE')")
  public E update(E entity) {
    E e = entityManager.merge(entity);
    entityManager.flush();
    return e;
  }

  @Override
  @PreAuthorize("hasAuthority('tenant:user')")
  public List<E> list() {
    return listQuery().getResultList();
  }

  @Override
  public E get(ID id) {
    return entityManager.find(entityType, id);
  }

  @Override
  public E delete(ID id) {
    E entity = entityManager.find(entityType, id);
    if (entity == null) {
      throw new EntityNotFoundException(
          String.format(
              "No %s " + "identified by '%s' was found for current user!", entityType, id));
    }
    entityManager.remove(entity);
    permissionService.delete(entityType, entity);
    return entity;
  }

  @Override
  @PreAuthorize("hasAuthority('tenant:user')")
  public E save(E entity) {
    long w = count(entity);
    //    if (w == 0) {
    return create(entity);
    //    } else {
    //      return update(entity);
    //    }
    //      return entity;
  }

  @PreAuthorize("hasAuthority('tenant:user')")
  public Long count() {
    final String template =
        String.format(
            "select count(e) from %s as e "
                + "join e.identity oid "
                + "where oid.owner.username = :id ",
            entityName);
    return entityManager
        .createQuery(template, Long.class)
        .setParameter("id", session.getUsername())
        .getSingleResult();
  }

  protected long count(E entity) {
    final String template =
        String.format(
            "select count(e) from %s as e "
                + "join e.identity oid "
                + "where oid.owner.username = :id "
                + "and e.id = :wid",
            entityName);
    final Long w =
        entityManager
            .createQuery(template, Long.class)
            .setParameter("id", session.getUsername())
            .setParameter("wid", idFor(entity))
            .getSingleResult();
    return w;
  }

  protected TypedQuery<E> listQuery() {

    final String template =
        "select e from %s e " + "join e.identity oid where " + "oid.owner.username = :id";

    final String query = String.format(template, entityName);
    return entityManager.createQuery(query, entityType).setParameter("id", session.getUsername());
  }

  protected Session getSession() {
    return session;
  }

  protected void flush() {
    entityManager.flush();
  }

  protected EntityManager getEntityManager() {
    return entityManager;
  }

  protected <T extends Persistable> void grant(
      Class<T> entityType, T instance, Permission... permissions) {
    permissionService.grantWithCurrentSession(entityType, instance, permissions);
  }

  @SuppressWarnings("unchecked")
  protected <T extends Persistable> void grant(T instance, Permission... permissions) {
    permissionService.grantWithCurrentSession((Class<T>) entityType, instance, permissions);
  }

  protected <T extends ProtectedDistributableEntity> void checkPermission(
      T instance, Permission... permissions) {
    permissionService.checkPermission(instance, permissions);
  }

  protected <T extends Persistable> void revokeAll(Class<T> type, T instance) {
    permissionService.delete(type, instance);
  }

  @SuppressWarnings("unchecked")
  protected <T extends Serializable> T idFor(E e) {
    if (e instanceof DistributableEntity || e instanceof DistributableHierarchicalEntity) {
      return (T) e.getId();
    }
    return (T) e.getId();
  }

  protected void revokeAll(E instance) {
    permissionService.delete(entityType, instance);
  }
}
