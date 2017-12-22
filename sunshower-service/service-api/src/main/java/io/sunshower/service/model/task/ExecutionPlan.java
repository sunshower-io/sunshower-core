package io.sunshower.service.model.task;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.IdentifierConverter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haswell on 2/8/17.
 */
@XmlRootElement(name = "tree")
public class ExecutionPlan {

    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(IdentifierConverter.class)
    private Identifier id;

    @XmlElement(name = "level")
    @XmlElementWrapper(name = "levels")
    private List<ExecutionLevel> levels = new ArrayList<>();
    

    @XmlTransient
    private transient Object payload;
    
    public ExecutionPlan() {

    }

    public ExecutionPlan(Identifier id, Object payload) {
        this.id = id;
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void addLevel(ExecutionLevel level) {
        levels.add(level);
    }

    public List<ExecutionLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<ExecutionLevel> levels) {
        this.levels = levels;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for(ExecutionLevel i : levels) {
            indent(i.getLevel(), b);
            b.append("Level { order = " + i.getLevel() + " \n");
            for(ExecutionTask t : i.getTasks()) {
                indent(i.getLevel() + 1, b);
                b.append(t);
                b.append("\n");
            }
        }
        return b.toString();
    }

    private void indent(int level, StringBuilder b) {
        for(int i = 0; i < level; i++) {
            b.append(" ");
        }
    }
}
