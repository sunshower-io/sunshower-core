package io.sunshower.model.core.auth;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import io.sunshower.common.Identifier;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;

/** Created by haswell on 2/20/17. */
public class SessionTest extends SerializationTestCase {

  public SessionTest() {
    super(SerializationAware.Format.JSON, true, Session.class);
  }

  @Test
  public void ensureSessionSessionIdIsSerializedCorrectly() {
    final Session<String> session = new Session<>();
    session.setSessionId("cool");
    Session copy = copy(session);
    assertThat(copy.getSessionId(), is("cool"));
  }

  @Test
  public void ensureSessionClassIsSerializedCorrectly() {

    final Session<String> session = new Session<>();
    session.setTargetType(String.class);
    session.setId(Identifier.random());
    Session copy = read(write(session), Session.class);

    write(session, System.out);
    assertEquals(String.class, copy.getTargetType());
  }

  @Test
  public void ensureSessionIdIsSerializedCorrectly() {
    final Identifier id = Identifier.random();
    final Session<String> session = new Session<>();
    session.setId(id);
    Session copy = copy(session);
  }
}
