package io.sunshower.service.hal.core;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/** Created by haswell on 5/22/17. */
@XmlEnum
public enum ElementType {
  @XmlEnumValue("vertex")
  Vertex,

  @XmlEnumValue("edge")
  Edge
}
