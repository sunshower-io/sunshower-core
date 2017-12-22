package io.sunshower.service.hal.core;


import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by haswell on 5/22/17.
 */
@XmlRootElement(name = "vertex")
public class Vertex extends AbstractElement<Vertex> {

    public Vertex() {
        super(Vertex.class);
    }

}
