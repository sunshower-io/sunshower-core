package io.sunshower.model.core.event;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/** Created by haswell on 2/19/17. */
public class EventTypeConverter extends XmlAdapter<String, Object> {

  @Override
  public Object unmarshal(String v) throws Exception {
    return null;
  }

  @Override
  public String marshal(Object v) throws Exception {
    return null;
  }
}
