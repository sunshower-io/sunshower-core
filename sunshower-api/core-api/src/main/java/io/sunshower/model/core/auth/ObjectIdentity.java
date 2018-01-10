package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.persistence.core.converters.IdentifierConverter;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "acl_object_identity", schema = Schemata.SUNSHOWER)
public class ObjectIdentity extends DistributableEntity {

  @Basic
  @Convert(converter = IdentifierConverter.class)
  @Column(name = "object_id_identity")
  private Identifier reference;

  @Basic
  @Column(name = "entries_inheriting")
  private boolean inheriting;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "object_id_class")
  private SecuredObject object;

  @ManyToOne
  @JoinColumn(name = "parent_object")
  private ObjectIdentity parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
  private Set<ObjectIdentity> children;

  @JoinColumn(name = "owner_sid")
  @OneToOne(fetch = FetchType.LAZY)
  private SecurityIdentity owner;

  public Identifier getReference() {
    return reference;
  }

  public void setReference(Identifier reference) {
    this.reference = reference;
  }

  public SecuredObject getObject() {
    return object;
  }

  public void setObject(SecuredObject object) {
    this.object = object;
  }

  public ObjectIdentity getParent() {
    return parent;
  }

  public void setParent(ObjectIdentity parent) {
    this.parent = parent;
  }

  public Set<ObjectIdentity> getChildren() {
    return children;
  }

  public void setChildren(Set<ObjectIdentity> children) {
    this.children = children;
  }

  public SecurityIdentity getOwner() {
    return owner;
  }

  public void setOwner(SecurityIdentity owner) {
    this.owner = owner;
  }

  public boolean isInheriting() {
    return inheriting;
  }

  public void setInheriting(boolean inheriting) {
    this.inheriting = inheriting;
  }
}
