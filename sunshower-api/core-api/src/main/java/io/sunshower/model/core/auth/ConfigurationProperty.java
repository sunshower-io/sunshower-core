package io.sunshower.model.core.auth;

import io.sunshower.model.core.AbstractProperty;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@XmlRootElement(name = "property")
@Table(name = "CONFIGURATION_PROPERTY", schema = "SUNSHOWER")
public class ConfigurationProperty extends AbstractProperty {

  public ConfigurationProperty(Type type, String key, String value) {
    super(type, key, value);
  }
}
