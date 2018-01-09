package io.sunshower.service.task.exec;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/** Created by haswell on 2/8/17. */
@XmlEnum
public enum EventClass {
  @XmlEnumValue("success")
  Success,
  @XmlEnumValue("failure")
  Failure,
  @XmlEnumValue("error")
  Error,
  @XmlEnumValue("suspend")
  Suspend,
  @XmlEnumValue("resume")
  Resume
}
