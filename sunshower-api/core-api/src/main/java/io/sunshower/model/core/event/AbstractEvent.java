package io.sunshower.model.core.event;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 2/19/17. */
@XmlRootElement(name = "event")
public class AbstractEvent extends DistributableEntity implements Event<String, String> {

  @XmlAttribute(name = "event-type")
  private String type;

  @XmlAttribute private String category;

  public AbstractEvent() {
    super();
  }

  public AbstractEvent(String type, String category) {
    this.type = type;
    this.category = category;
  }

  public AbstractEvent(Identifier id, String type, String category) {
    super(id);
    this.type = type;
    this.category = category;
  }

  @Override
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
