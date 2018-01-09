package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 10/13/17. */
@XmlRootElement(name = "element-summary")
public class ElementSummary {

  @XmlList
  @XmlElement(name = "identifiers")
  private List<Identifier> identifiers;

  public List<Identifier> getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(List<Identifier> identifiers) {
    this.identifiers = identifiers;
  }

  public boolean contains(Identifier nodeId) {
    return identifiers != null && identifiers.contains(nodeId);
  }
}
