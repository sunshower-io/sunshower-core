package io.sunshower.service.model.properties;

import io.sunshower.service.model.Property;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property")
public class StringProperty extends Property<String, StringProperty> {

  public StringProperty() {
    this(null);
  }

  public StringProperty(String key) {
    this(key, key);
  }

  public StringProperty(String key, String name) {
    this(key, name, null);
  }

  public StringProperty(String key, String name, String value) {
    super(StringProperty.class, name, key, value);
  }
}
