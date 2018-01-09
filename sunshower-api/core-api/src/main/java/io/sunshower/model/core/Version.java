package io.sunshower.model.core;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;

/** Created by haswell on 10/26/16. */
@Entity
@IdClass(Identifier.class)
public class Version {

  @Id private byte[] id;

  @Basic
  @Column(name = "major")
  private Integer major;

  @Basic
  @Column(name = "minor")
  private Integer minor;

  @Basic
  @Column(name = "minor_minor")
  private Integer minorMinor;

  @Basic
  @Column(name = "extension")
  private String extension;

  public Version() {
    setId(DistributableEntity.sequence.next());
  }

  public Identifier getId() {
    if (id != null) {
      return Identifier.valueOf(id);
    }
    return null;
  }

  public void setId(Identifier id) {
    if (id != null) {
      this.id = id.value();
    }
  }

  public Integer getMajor() {
    return major;
  }

  public void setMajor(Integer major) {
    this.major = major;
  }

  public Integer getMinor() {
    return minor;
  }

  public void setMinor(Integer minor) {
    this.minor = minor;
  }

  public Integer getMinorMinor() {
    return minorMinor;
  }

  public void setMinorMinor(Integer minorMinor) {
    this.minorMinor = minorMinor;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public String toString() {
    return String.format("%d.%d.%d-%s", major, minor, minorMinor, extension);
  }
}
