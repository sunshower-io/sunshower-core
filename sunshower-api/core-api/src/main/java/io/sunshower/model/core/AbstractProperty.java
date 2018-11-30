package io.sunshower.model.core;

import io.sunshower.common.Identifier;
import io.sunshower.persist.Identifiers;
import io.sunshower.persist.Sequence;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@XmlRootElement(name = "property")
public class AbstractProperty extends DistributableEntity {

  public enum Type implements PropertyType {
    Integer,
    String,
    Boolean,
    Secret,
    Class,
    Time
  }

  @Basic
  @XmlElement
  @Column(name = "property_key")
  private String key;

  @Basic
  @XmlElement
  @Column(name = "name")
  private String name;

  @Basic @XmlElement private String value;

  @Enumerated
  @Column(name = "type")
  @XmlAttribute(name = "property-type")
  private Type propertyType = Type.String;

  static final Sequence<Identifier> seq = Identifiers.newSequence(true);

  public AbstractProperty() {
    setId(seq.next());
  }

  public AbstractProperty(Type type, String key, String value) {
    this(type, key, null, value);
  }

  public AbstractProperty(Type type, String key, String name, String value) {
    setId(seq.next());
    setPropertyType(type);
    setKey(key);
    setName(name);
    setValue(value);
  }

  public void setValue(String value) {
    if (value == null) {
      return;
    }
    switch (propertyType) {
      case Integer:
        Long.parseLong(value);
        break;
      case Boolean:
        Boolean.parseBoolean(value);
        break;
    }
    this.value = value;
  }

  public boolean isResolved() {
    return getValue() != null;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
