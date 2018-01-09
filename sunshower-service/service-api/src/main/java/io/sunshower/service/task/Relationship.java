package io.sunshower.service.task;

import io.sunshower.persistence.core.DistributableEntity;

/** Created by haswell on 3/27/17. */
public class Relationship extends DistributableEntity {
  private String key;

  public Relationship() {}

  public Relationship(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
