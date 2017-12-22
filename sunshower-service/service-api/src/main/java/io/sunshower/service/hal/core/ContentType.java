package io.sunshower.service.hal.core;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Created by haswell on 10/13/17.
 */
@XmlEnum
public enum ContentType {
  
    @XmlEnumValue("file")
    File,

    @XmlEnumValue("reference")
    Reference
}
