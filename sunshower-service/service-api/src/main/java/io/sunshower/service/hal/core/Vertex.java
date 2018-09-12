package io.sunshower.service.hal.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlRootElement(name = "vertex")
public class Vertex extends AbstractElement<Vertex> {

  public Vertex() {
    super(Vertex.class);
  }

  @XmlElement(name = "reference")
  private Reference reference;
}
