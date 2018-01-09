package io.sunshower.service.model.properties;

import io.sunshower.service.model.Property;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property")
public class BooleanProperty extends Property<Boolean, BooleanProperty> {

  public BooleanProperty() {
    this(null);
  }

  public BooleanProperty(String key) {
    this(key, key);
  }

  public BooleanProperty(String key, String name) {
    this(key, name, null);
  }

  public BooleanProperty(String key, String name, Boolean value) {
    super(BooleanProperty.class, name, key, value);
  }
}
