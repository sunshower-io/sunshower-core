package io.sunshower.scopes.conversation;

import io.sunshower.scopes.AbstractScopeTest;
import lombok.val;
import org.junit.jupiter.api.Test;

class ConversationScopeTest extends AbstractScopeTest {


    @Test
    void ensureFiringLogoutEventWorks() throws InterruptedException {
        start();
        val event = new ConversationInitiatedEvent();
        publisher.publishEvent(event);
    }

}