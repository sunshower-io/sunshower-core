package io.sunshower.service.hal.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

/** Created by haswell on 5/22/17. */
@XmlRootElement(name = "layout")
public class Layout {

  @XmlAttribute private double x;

  @XmlAttribute private double y;

  @XmlAttribute private double width;

  @XmlAttribute private double height;

  @XmlInverseReference(mappedBy = "layout")
  private Element element;

  public Layout() {}

  public Layout(Element host) {
    this.element = host;
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
    this.element = element;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }
}
