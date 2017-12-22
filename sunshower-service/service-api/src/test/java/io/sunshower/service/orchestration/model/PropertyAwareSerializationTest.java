package io.sunshower.service.orchestration.model;

import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.hal.core.Vertex;
import io.sunshower.service.model.PropertyAwareObject;
import io.sunshower.service.model.properties.IntegerProperty;
import io.sunshower.service.model.properties.StringProperty;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitPlatform.class)
public class PropertyAwareSerializationTest extends SerializationTestCase {

    public PropertyAwareSerializationTest() {
        super(SerializationAware.Format.XML, new Class<?>[]{
                Graph.class,
                StringProperty.class,
                IntegerProperty.class,
                PropertyAwareObject.class
        });
    }
    
    @Test
    public void ensureWritingGraphWorks() {
        final Graph g = new Graph();
        g.setName("frapper");
        Vertex v = new Vertex();
        v.setName("frapperv");
        g.addVertex(v);
        final Graph copy = copy(g);
        assertThat(copy.getVertices().size(), is(1));
    }
    
    @Test
    public void ensureAddingStringPropertyToGraphWorks() {
        final Graph g = new Graph();
        g.setName("frapper");
        
        g.addProperty(new StringProperty("cool"));
        write(g, System.out);
        assertThat(copy(g).getProperties().size(), is(1));
    }
}
