package io.sunshower.service.task;

import io.sunshower.common.Identifier;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node {

  @XmlAttribute(name = "name")
  private String name;

  @XmlAttribute private String key;

  @XmlAttribute(name = "id")
  private Identifier id;

  @XmlAnyElement(lax = true)
  private Object value;

  public Identifier getId() {
    return id;
  }

  public void setId(Identifier id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public boolean isValueRaw() {
    return value != null && value instanceof org.w3c.dom.Node;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "Node{"
        + "name='"
        + name
        + '\''
        + ", key='"
        + key
        + '\''
        + ", id="
        + id
        + ", value="
        + value
        + '}';
  }
}
