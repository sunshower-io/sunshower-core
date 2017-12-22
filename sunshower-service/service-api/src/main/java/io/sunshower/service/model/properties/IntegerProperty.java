package io.sunshower.service.model.properties;

import io.sunshower.service.model.Property;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property")
public class IntegerProperty extends Property<Long, IntegerProperty> {
   
   
    public IntegerProperty() {
        this(null, null, null);
        
    }

    public IntegerProperty(String key) {
        this(key, key);
    }

    public IntegerProperty(String key, String name) {
        this(key, name, null);
    }
    
    public IntegerProperty(String name, String key, Long value) {
        super(IntegerProperty.class, name, key, value);
    }

}
