package io.sunshower.service.task;

import io.sunshower.common.Identifier;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 3/26/17. */
@XmlRootElement(name = "edge")
public class Edge {

  @XmlAttribute private Identifier id;

  @XmlAttribute(name = "source")
  private Identifier source;

  @XmlAttribute(name = "target")
  private Identifier target;

  @XmlAttribute(name = "relationship")
  private Identifier relationship;

  public Identifier getId() {
    return id;
  }

  public void setId(Identifier id) {
    this.id = id;
  }

  public Identifier getSource() {
    return source;
  }

  public void setSource(Identifier source) {
    this.source = source;
  }

  public Identifier getTarget() {
    return target;
  }

  public void setTarget(Identifier target) {
    this.target = target;
  }

  public Identifier getRelationship() {
    return relationship;
  }

  public void setRelationship(Identifier relationship) {
    this.relationship = relationship;
  }
}
