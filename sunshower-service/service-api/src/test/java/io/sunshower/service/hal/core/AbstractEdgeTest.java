package io.sunshower.service.hal.core;

import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(JUnitPlatform.class)
public class AbstractEdgeTest extends SerializationTestCase {

    public AbstractEdgeTest() {
        super(SerializationAware.Format.JSON,
                AbstractElement.class,
                Edge.class,
                Vertex.class,
                Properties.class,
                Layout.class,
                TestElement.class
        );
    }

    @Test
    public void ensureEdgesAreWrittenCorrectly() {
        Edge testElementAbstractEdge = new Edge();
        testElementAbstractEdge.setSource(new Vertex());
        testElementAbstractEdge.setTarget(new Vertex());
        testElementAbstractEdge.setCreated(new Date());
        testElementAbstractEdge.setModified(new Date());
        write(testElementAbstractEdge, System.out);

    }

}