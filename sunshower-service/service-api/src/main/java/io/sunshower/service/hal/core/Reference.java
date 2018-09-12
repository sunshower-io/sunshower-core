package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.ClassAdapter;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Getter
@Setter
@XmlRootElement(name = "reference")
public class Reference {

  @XmlElement(name = "reference-type")
  @XmlJavaTypeAdapter(ClassAdapter.class)
  private Class<?> referenceType;

  @XmlAttribute(name = "target-key")
  private String targetKey;

  @XmlAttribute(name = "namespace")
  private String namespace;

  @XmlAttribute(name = "target-id")
  private Identifier targetId;
}
