package io.sunshower.model.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PropertyTest {

    @Test
    void testAllThePropertiesAtOnce() {
        for(Property.Type t : Property.Type.values()) {
            Property p;
            switch(t) {
                case Integer: p = Property.integer("schnorp", "porp", "10"); break;
                case Secret: p = Property.secret("schnorp", "porp", t.toString()); break;
                case Boolean: p = Property.bool("schnorp", "porp", "true"); break;
                case Class: p = Property.type("schnorp", "porp", t.toString()); break;
                default: p = Property.string("schnorp", "porp", t.toString()); break;
            }
            assertThat(p.getPropertyType(), is(t));
        }
    }
}