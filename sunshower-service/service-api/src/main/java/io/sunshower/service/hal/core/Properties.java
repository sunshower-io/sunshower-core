package io.sunshower.service.hal.core;

import io.sunshower.common.rs.MapAdapter;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by haswell on 5/22/17.
 */
@XmlRootElement(name = "properties")
public class Properties {

    @XmlElement
    @XmlInverseReference(mappedBy = "properties")
    private Element element;

    @XmlElement
    @XmlAnyElement(lax = true)
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, Object> properties;


    public Properties() {

    }

    public Properties(Element element) {
        Objects.requireNonNull(element, "Element may not be null using this constructor!");
    }

    public <T> void put(String key, T value) {
        checkProperties();
        properties.put(key, value);
    }


    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        checkProperties();
        return (T) properties.get(key);

    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        checkProperties();
        return (T) properties.get(key);
    }


    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public boolean contains(String key) {
        return properties != null && properties.containsKey(key);
    }

    private void checkProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
    }
}
