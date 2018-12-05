package io.sunshower.scopes.conversation;

import io.sunshower.scopes.AbstractScopeTest;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationScopeTest extends AbstractScopeTest {

  @BeforeEach
  public void setUp() {
    super.setUp();
    start();
  }

  @AfterEach
  public void tearDown() {
    stop();
  }

  @Test
  void ensureFiringLogoutEventWorks() {
    val event = new ConversationInitiatedEvent("hello");
    publisher.publishEvent(event);
  }
}
