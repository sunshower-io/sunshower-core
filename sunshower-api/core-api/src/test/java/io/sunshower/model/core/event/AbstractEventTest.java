package io.sunshower.model.core.event;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import io.sunshower.common.Identifier;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import javax.xml.bind.annotation.*;
import org.junit.jupiter.api.Test;

public class AbstractEventTest extends SerializationTestCase {

  public AbstractEventTest() {
    super(SerializationAware.Format.JSON, Event.class, Category.class);
  }

  @XmlEnum
  public enum Category {
    @XmlEnumValue("fst")
    Fst,
    @XmlEnumValue("snd")
    Snd
  }

  @XmlRootElement(name = "e")
  public static class Event extends AbstractEvent {

    public Event() {}

    public Event(String type, String category) {
      super(type, category);
    }

    public Event(Identifier id, String type, String category) {
      super(id, type, category);
    }
  }

  @Test
  public void ensureWritingEventProducesExpectedResults() {
    Event whatever = copy(new Event(Category.Fst.toString(), "whatever"));
    assertThat(whatever.getType(), is(Category.Fst.toString()));
  }

  @Test
  public void ensureWritingEventCopiesCategory() {
    Event whatever = copy(new Event(Category.Fst.toString(), "whatever"));
    assertThat(whatever.getCategory(), is("whatever"));
  }
}
