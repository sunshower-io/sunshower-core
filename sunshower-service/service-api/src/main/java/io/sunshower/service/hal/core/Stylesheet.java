package io.sunshower.service.hal.core;

import io.sunshower.common.rs.MapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haswell on 5/22/17.
 */
@XmlRootElement(name = "stylesheet")
public class Stylesheet {

    @XmlElement(name = "styles")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, String> styles;

    public Map<String, String> getStyles() {
        return styles;
    }

    public void setStyles(Map<String, String> styles) {
        this.styles = styles;
    }

    public void set(String key, String value) {
        if(styles == null) {
            styles = new HashMap<>();
        }
        styles.put(key, value);
    }

    public String get(String key) {
        if(styles == null) {
            styles = new HashMap<>();
        }
        return styles.get(key);
    }
}
