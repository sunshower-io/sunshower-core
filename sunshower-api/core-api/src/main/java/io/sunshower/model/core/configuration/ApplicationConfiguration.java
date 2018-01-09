package io.sunshower.model.core.configuration;

import io.sunshower.common.Identifier;
import io.sunshower.persist.internal.jaxb.IdentifierAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "configuration")
public class ApplicationConfiguration {

  @XmlAttribute
  @XmlJavaTypeAdapter(IdentifierAdapter.class)
  private Identifier id;

  @XmlElement private String name;

  public ApplicationConfiguration() {
    this(Identifier.random());
  }

  public ApplicationConfiguration(Identifier uuid) {
    this.id = uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ApplicationConfiguration)) return false;

    ApplicationConfiguration that = (ApplicationConfiguration) o;
    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
