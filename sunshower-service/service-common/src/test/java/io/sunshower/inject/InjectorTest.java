package io.sunshower.inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;
import org.junit.jupiter.api.Test;

public class InjectorTest {

  @Test
  @SuppressWarnings("unchecked")
  public void ensureInjectingClassWorks() {

    class A {
      @Inject private String name;
    }

    final Injector injector = new Injector<>(Inject.class, new FieldNameResolver());
    injector.register(String.class, "Josiah");
    final A a = new A();
    injector.inject(A.class, a);
    assertThat(a.name, is("Josiah"));
  }
}
