package io.sunshower.model.core;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@XmlRootElement(name = "property")
@Table(name = "PROPERTY", schema = "SUNSHOWER")
public class Property extends AbstractProperty {
  {
    setType(Property.class);
  }

  Property() {}

  public Property(Type type, String key, String value) {
    super(type, key, value);
  }

  public Property(Type type, String key, String name, String value) {
    super(type, key, name, value);
  }

  public static Property string(String key, String name, String value) {
    return new Property(Type.String, key, name, value);
  }

  public static Property integer(String key, String name, String value) {
    return new Property(Type.Integer, key, name, value);
  }

  public static AbstractProperty secret(String key, String name, String value) {
    return new Property(Type.Secret, key, name, value);
  }

  public static AbstractProperty bool(String key, String name, String value) {
    return new Property(Type.Secret, key, name, value);
  }
}
