package io.sunshower.model.core.io;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.persistence.core.Hierarchical;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

/** @author haswell */
@Entity
@XmlRootElement(name = "file")
@Table(name = "FILE", schema = Schemata.SUNSHOWER)
public class File extends DistributableEntity implements Hierarchical<Identifier, File> {

  @Basic @XmlElement private String path;

  @XmlIDREF
  @ManyToOne
  @JoinColumn(name = "parent_id")
  private File parent;

  @Basic @XmlAttribute private String extension;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @XmlElement(name = "child")
  @JoinColumn(name = "parent_id")
  @XmlElementWrapper(name = "children")
  private Set<File> children;

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public File() {}

  public File(String name) {
    setPath(name);
  }

  public String getPath() {
    return path;
  }

  public void setPath(String name) {
    this.path = name;
    if (name != null) {
      final int idx = name.lastIndexOf('.');
      if (idx > 0) {
        final String ext = name.substring(idx + 1, name.length());
        if (ext.length() < 10) {
          setExtension(ext);
        }
      }
    }
  }

  public void setChildren(Set<File> children) {
    this.children = children;
  }

  @Override
  public File getParent() {
    return parent;
  }

  @Override
  public Collection<File> getChildren() {
    if (children == null) {
      return (children = new HashSet<>());
    }
    return children;
  }

  @Override
  public boolean addChild(File file) {
    return getChildren().add(file);
  }

  @Override
  public void setParent(File file) {
    parent = file;
  }
}
