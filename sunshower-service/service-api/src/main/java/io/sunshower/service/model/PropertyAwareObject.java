package io.sunshower.service.model;


import io.sunshower.common.rs.ClassAdapter;
import io.sunshower.common.rs.TypeAttributeClassExtractor;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "property-aware")
@XmlClassExtractor(TypeAttributeClassExtractor.class)
public class PropertyAwareObject<T extends PropertyAwareObject<T>> extends BaseModelObject {
  
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(ClassAdapter.class)
    private Class<T> type;

    /**
     * TODO:  figure if we want these in the database or in the property graph.  There are advantages to 
     * having them in the property graph, such as automatically getting revision control.  On the other hand, JAXB inheritance 
     * sucks :(
     */

    @Transient
    @OneToMany(
            fetch = FetchType.LAZY, 
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "ENTITY_TO_PROPERTIES",
            joinColumns = {
                    @JoinColumn(
                            name = "entity_id", 
                            referencedColumnName = "id"
                    )
            },
            inverseJoinColumns = 
                @JoinColumn(
                        name = "property_id", 
                        referencedColumnName = "id"
                )
    )
    @XmlElement(name = "property")
    @XmlElementWrapper(name = "properties")
    private List<Property<?, ?>> properties;
    
    protected PropertyAwareObject() {
        
    }
    
    protected PropertyAwareObject(Class<T> type) {
        this.type = type;
    }
   
    
    public void addProperty(Property<?, ?> property) {
        if(property == null) {
            return;
        }
        if(properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(property);
    }
    
    public void removeProperty(Property<?, ?> property) {
        if(property == null) {
            return;
        }
        if(properties == null) {
            return;
        }
        properties.remove(property);
    }
    
    
    public List<Property<?, ?>> getProperties() {
        return properties == null ? Collections.emptyList() : Collections.unmodifiableList(properties);
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
    
    public void clearProperties() {
        properties = new ArrayList<>();
    }
}
