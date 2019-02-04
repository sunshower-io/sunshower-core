package io.sunshower.model.core.auth;

import io.sunshower.model.core.AbstractProperty;
import io.sunshower.model.core.Property;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.*;
import javax.persistence.*;
import lombok.val;

@Embeddable
public abstract class Configuration extends DistributableEntity {

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @MapKeyJoinColumn(name = "property_key")
  private Map<String, ConfigurationProperty> properties;

  public void setValue(Property.Type type, String key, String value) {
    if (properties == null) {
      properties = new HashMap<>();
    }
    properties.put(key, new ConfigurationProperty(type, key, value));
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue(String key) {
    if (properties == null) {
      return null;
    }
    val prop = properties.get(key);
    if (prop == null) {
      return null;
    }

    return propertyValue(prop);
  }

  public List<AbstractProperty> getProperties() {
    return properties == null ? Collections.emptyList() : new ArrayList<>(properties.values());
  }

  public boolean addProperty(AbstractProperty property) {
    if (properties == null) {
      properties = new HashMap<>();
    }
    return properties.put(
            property.getKey(),
            new ConfigurationProperty(
                property.getPropertyType(), property.getKey(), property.getValue()))
        != null;
  }

  public void clear() {
    if (properties != null) {
      properties.clear();
    }
  }

  public <T> T clearValue(String key) {
    if (properties != null) {
      val result = properties.remove(key);
      if (result != null) {
        return propertyValue(result);
      }
    }
    return null;
  }

  private <T> T propertyValue(AbstractProperty prop) {
    val type = prop.getPropertyType();
    switch (type) {
      case Secret:
      case String:
        return (T) prop.getValue();
      case Boolean:
        return (T) Boolean.valueOf(prop.getValue());
      case Integer:
        return (T) Integer.valueOf(prop.getValue());
      default:
        return (T) prop.getValue();
    }
  }
}
