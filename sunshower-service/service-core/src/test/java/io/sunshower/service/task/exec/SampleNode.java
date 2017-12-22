package io.sunshower.service.task.exec;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by haswell on 3/26/17.
 */
@XmlRootElement(name = "sample-node")
public class SampleNode {

    @XmlAttribute
    private String name;


    public SampleNode() {

    }

    public SampleNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
