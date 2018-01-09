package io.sunshower.service.model;

import java.util.Date;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/** Created by haswell on 5/9/17. */
public class UpdateListener {

  @PreUpdate
  @PrePersist
  public void setLastModified(Updatable updatable) {
    updatable.setLastModified(new Date());
  }
}
