package io.sunshower.service.hal.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 5/22/17. */
@XmlRootElement(name = "global-layout")
public class GlobalLayout {

  @XmlAttribute(name = "scale-x")
  private double scaleX;

  @XmlAttribute(name = "scale-y")
  private double scaleY;

  @XmlAttribute(name = "pan-x")
  private double panX;

  @XmlAttribute(name = "pan-y")
  private double panY;

  public double getScaleX() {
    return scaleX;
  }

  public void setScaleX(double scaleX) {
    this.scaleX = scaleX;
  }

  public double getScaleY() {
    return scaleY;
  }

  public void setScaleY(double scaleY) {
    this.scaleY = scaleY;
  }

  public double getPanX() {
    return panX;
  }

  public void setPanX(double panX) {
    this.panX = panX;
  }

  public double getPanY() {
    return panY;
  }

  public void setPanY(double panY) {
    this.panY = panY;
  }
}
