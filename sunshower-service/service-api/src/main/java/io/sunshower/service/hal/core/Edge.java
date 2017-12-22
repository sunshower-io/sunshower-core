package io.sunshower.service.hal.core;

import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by haswell on 5/22/17.
 */
@XmlRootElement(name = "abstract-edge")
@XmlDiscriminatorValue("abstract-edge")
public class Edge extends AbstractElement<Edge> {


    @XmlIDREF
    @XmlElement
    private Vertex target;

    @XmlIDREF
    @XmlElement
    private Vertex source;

    @XmlAttribute
    private String relationship;

    @SuppressWarnings("unchecked")
    public Edge() {
        super(Edge.class);
    }


    public Vertex getSource() {
        return source;
    }

    public  Vertex getTarget() {
        return target;
    }


    public  void setSource(Vertex source) {
        this.source = source;
    }

    public  void setTarget(Vertex target) {
        this.target = target;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
