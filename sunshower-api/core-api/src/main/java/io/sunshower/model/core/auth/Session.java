package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.ClassAdapter;
import io.sunshower.persist.internal.jaxb.IdentifierAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/** Created by haswell on 2/20/17. */
@XmlRootElement
public class Session<T> {

  @XmlAttribute
  @XmlJavaTypeAdapter(IdentifierAdapter.class)
  private Identifier id;

  @XmlAttribute(name = "target-id")
  @XmlJavaTypeAdapter(IdentifierAdapter.class)
  private Identifier targetId;

  @XmlAttribute(name = "session-id")
  private String sessionId;

  @XmlElement(name = "target-type")
  @XmlJavaTypeAdapter(ClassAdapter.class)
  private Class<T> targetType;

  public Identifier getId() {
    return id;
  }

  public void setId(Identifier id) {
    this.id = id;
  }

  public Identifier getTargetId() {
    return targetId;
  }

  public void setTargetId(Identifier targetId) {
    this.targetId = targetId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Class<T> getTargetType() {
    return targetType;
  }

  public void setTargetType(Class<T> targetType) {
    this.targetType = targetType;
  }
}
