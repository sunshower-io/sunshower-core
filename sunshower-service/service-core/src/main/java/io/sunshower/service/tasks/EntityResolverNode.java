package io.sunshower.service.tasks;

import io.sunshower.common.Identifier;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 3/27/17. */
@XmlRootElement(name = "resolve-entity")
public class EntityResolverNode {

  @XmlAttribute private Identifier id;

  @XmlAttribute private Class<?> type;

  public Identifier getId() {
    return id;
  }

  public void setId(Identifier id) {
    this.id = id;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }
}
