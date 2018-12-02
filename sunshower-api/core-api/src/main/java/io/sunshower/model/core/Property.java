package io.sunshower.model.core;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.TypeAttributeClassExtractor;
import io.sunshower.persist.Identifiers;
import io.sunshower.persist.Sequence;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;

@Entity
@Getter
@Setter
@XmlRootElement(name = "property")
@Table(name = "PROPERTY", schema = "SUNSHOWER")
@XmlClassExtractor(TypeAttributeClassExtractor.class)
public class Property extends DistributableEntity {

  public enum Type implements PropertyType {
    Integer,
    String,
    Boolean,
    Secret,
    Class
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

  @Transient
  @XmlAttribute(name = "type")
  private Class<?> type = Property.class;

  static final Sequence<Identifier> seq = Identifiers.newSequence(true);

  public Property() {
    setId(seq.next());
  }

  public static Property string(String key, String name, String value) {
    return new Property(Type.String, key, name, value);
  }

  public static Property integer(String key, String name, String value) {
    return new Property(Type.Integer, key, name, value);
  }

  public static Property secret(String key, String name, String value) {
    return new Property(Type.Secret, key, name, value);
  }

  public static Property bool(String key, String name, String value) {
    return new Property(Type.Boolean, key, name, value);
  }

  public static Property type(String key, String name, String value) {
    return new Property(Type.Class, key, name, value);
  }

  public Property(Type type, String key, String name, String value) {
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
