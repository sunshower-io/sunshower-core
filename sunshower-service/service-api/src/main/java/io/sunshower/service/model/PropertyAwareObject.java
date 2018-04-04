package io.sunshower.service.model;

import io.sunshower.common.rs.ClassAdapter;
import io.sunshower.common.rs.TypeAttributeClassExtractor;
import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;

@MappedSuperclass
@XmlRootElement(name = "property-aware")
@XmlClassExtractor(TypeAttributeClassExtractor.class)
public class PropertyAwareObject<T extends PropertyAwareObject<T>> extends BaseModelObject {

  @XmlAttribute(name = "type")
  @XmlJavaTypeAdapter(ClassAdapter.class)
  private Class<?> type;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
    schema = "SUNSHOWER",
    name = "ENTITY_TO_PROPERTIES",
    joinColumns = {@JoinColumn(name = "entity_id", referencedColumnName = "id")},
    inverseJoinColumns = @JoinColumn(name = "property_id", referencedColumnName = "id")
  )
  @XmlElement(name = "property")
  @XmlElementWrapper(name = "properties")
  @MapKeyJoinColumn(name = "properties_key")
  private Map<String, Property> properties;

  protected PropertyAwareObject() {}

  protected PropertyAwareObject(Class<T> type) {
    this.type = type;
  }

  public void addProperty(Property property) {
    if (property == null) {
      return;
    }
    if (properties == null) {
      properties = new LinkedHashMap<>();
    }
    properties.put(property.getKey(), property);
  }

  public void removeProperty(Property property) {
    if (property == null) {
      return;
    }
    if (properties == null) {
      return;
    }
    properties.remove(property.getKey());
  }

  public Property getProperty(String name) {
    if (properties != null) {
      return properties.get(name);
    }
    return null;
  }

  public <T> void setRole(Class<T> role) {
    if (role == null) {
      if (properties != null) {
        properties.remove("role");
      }
    } else {
      addProperty(new Property(Property.Type.Class, "role", "role", role.getName()));
    }
  }

  public <T> void setProperty(Class<T> property, String value) {
    addProperty(new Property(Property.Type.Class, property.getName(), "", value));
  }

  public List<Property> getProperties() {
    return properties == null
        ? Collections.emptyList()
        : Collections.unmodifiableList(new ArrayList<>(properties.values()));
  }

  @SuppressWarnings("unchecked")
  public Class<T> getType() {
    return (Class<T>) type;
  }

  public void setType(Class<T> type) {
    this.type = type;
  }

  public void clearProperties() {
    properties = new LinkedHashMap<>();
  }

  public <T> Property getProperty(Class<T> property) {
    return getProperty(property.getName());
  }

  @SuppressWarnings("unchecked")
  public <T> Class<T> getRole() {
    final Property role = getProperty("role");
    if (role == null) {
      return null;
    }
    final String r = role.getValue();

    try {
      return (Class<T>) Class.forName(r, false, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
