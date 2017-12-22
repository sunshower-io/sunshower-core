package io.sunshower.inject;


import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(JUnitPlatform.class)
public class InjectorTest {

    @Test
    @SuppressWarnings("unchecked")
    public void ensureInjectingClassWorks() {

        class A {
            @Inject
            private String name;
        }

        final Injector injector = new Injector<>(
                Inject.class,
                new FieldNameResolver()
        );
        injector.register(String.class, "Josiah");
        final A a = new A();
        injector.inject(A.class, a);
        assertThat(a.name, is("Josiah"));


    }

}