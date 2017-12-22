package io.sunshower.service.hal.core;

import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class AbstractElementTest extends SerializationTestCase {

    public AbstractElementTest() {
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
    public void ensureWritingComplexElementToJsonWorks() {
        final AbstractElement e = new AbstractElement();
        e.addElementProperty("frap", "adap");
    }

}