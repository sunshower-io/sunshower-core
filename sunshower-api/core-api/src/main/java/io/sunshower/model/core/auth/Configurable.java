package io.sunshower.model.core.auth;

import io.sunshower.model.core.AbstractProperty;
import io.sunshower.model.core.Property;
import java.util.Collections;
import java.util.List;
import lombok.val;

public interface Configurable {
  Configuration getConfiguration();

  void setConfiguration(Configuration cfg);

  default <T> void setConfigurationValue(Property.Type type, String key, T value) {
    var configuration = getConfiguration();
    configuration.setValue(type, key, String.valueOf(value));
  }

  default <T> T getConfigurationValue(String key) {
    val cfg = getConfiguration();
    if (cfg != null) {
      return cfg.getValue(key);
    }
    return null;
  }

  default List<AbstractProperty> getConfigurationValues() {
    val configuration = getConfiguration();
    return configuration == null ? Collections.emptyList() : configuration.getProperties();
  }

  public default <T> T clearValue(String key) {
    val configuration = getConfiguration();
    if (configuration == null) {
      return null;
    }
    return configuration.clearValue(key);
  }

  default void clearConfiguration() {
    val configuration = getConfiguration();
    if (configuration != null) {
      configuration.clear();
    }
  }
}
