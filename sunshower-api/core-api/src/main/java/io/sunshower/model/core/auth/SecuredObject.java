package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.persistence.core.converters.ClassConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "acl_class", schema = Schemata.SUNSHOWER)
public class SecuredObject extends DistributableEntity {

  @Column(name = "class")
  @Convert(converter = ClassConverter.class)
  private Class<?> type;

  public SecuredObject() {}

  public SecuredObject(final Class<?> type) {
    this.type = type;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }
}
