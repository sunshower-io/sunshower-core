package io.sunshower.service.model;

import io.sunshower.common.rs.ClassAdapter;
import io.sunshower.common.rs.TypeAttributeClassExtractor;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

@XmlRootElement(name = "property")
@XmlClassExtractor(TypeAttributeClassExtractor.class)
public abstract class Property<T, U extends Property<T, U>> extends DistributableEntity {

  @Transient
  @XmlAnyElement(lax = true)
  private T value;

  @Basic
  @XmlElement
  @Column(name = "property_key")
  private String key;

  @Basic
  @XmlElement
  @Column(name = "name")
  private String name;

  @Transient
  @XmlAttribute(name = "type")
  @XmlJavaTypeAdapter(ClassAdapter.class)
  private Class<U> type;

  @SuppressWarnings("unchecked")
  protected Property() {
    setType((Class) getClass());
  }

  protected Property(Class<U> type, String key, String name, T value) {
    setType(type);
    setKey(key);
    setName(name);
    setValue(value);
  }

  public boolean isResolved() {
    return value != null;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class<U> getType() {
    return type;
  }

  public void setType(Class<U> type) {
    this.type = type;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  protected void extract(Node value) {
    final NodeList list = (NodeList) value;
    Text item = (Text) list.item(0);
    setValue(extractValue(item.getWholeText()));
  }

  @SuppressWarnings("unchecked")
  private T extractValue(String nodeText) {
    return (T) nodeText;
  }

  public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (value instanceof Node) {
      extract((Node) value);
    }
  }
}
