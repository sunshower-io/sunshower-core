package io.sunshower.scopes.conversation;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.AbstractSessionAwareScope;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;

@Slf4j
public class ConversationScope extends AbstractSessionAwareScope
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {

  public ConversationScope(Cache cache, Session session, KeyProvider keyProvider) {
    super(cache, session, keyProvider);
  }

  @EventListener(ConversationInitiatedEvent.class)
  public void onConversationInitiated(ConversationInitiatedEvent event) {
    super.
  }

  @EventListener(ConversationFinalizedEvent.class)
  public void onConversationFinalized(ConversationFinalizedEvent event) {
    System.out.println("GOT two");
  }
}
