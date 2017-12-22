package io.sunshower.service.model.properties;

import io.sunshower.service.model.Property;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property")
public class SecretProperty extends Property<String, SecretProperty>{

    public SecretProperty() {
        this(null);
    }

    public SecretProperty(String key) {
        this(key, key);
    }

    public SecretProperty(String key, String name) {
        this(key, name, null);
    }

    public SecretProperty(String key, String name, String value) {
        super(SecretProperty.class, name, key, value);
    }
}
