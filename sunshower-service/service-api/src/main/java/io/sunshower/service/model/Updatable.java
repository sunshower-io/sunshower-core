package io.sunshower.service.model;

import java.util.Date;

/** Created by haswell on 5/9/17. */
public interface Updatable {

  void setLastModified(Date date);
}
