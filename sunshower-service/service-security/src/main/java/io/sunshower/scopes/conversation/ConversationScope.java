package io.sunshower.scopes.conversation;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.AbstractDynamicScope;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;

@Slf4j
public class ConversationScope extends AbstractDynamicScope
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {

  public ConversationScope(Cache cache, Session session, KeyProvider keyProvider) {
    super(cache, session, keyProvider);
  }

  @Override
  protected int getMaxSize() {
    return 10;
  }

  @EventListener(ConversationInitiatedEvent.class)
  public void onConversationInitiated(ConversationInitiatedEvent event) {
    log.trace("Conversation with id {} initialized", event.getId());
    createScopeRegion(event.getId());
  }


  @EventListener(ConversationFinalizedEvent.class)
  public void onConversationFinalized(ConversationFinalizedEvent event) {
    log.trace("Conversation with id {} finalized", event.getId());
  }

  @EventListener(ConversationCancelledEvent.class)
  public void onConversationCancelled(ConversationFinalizedEvent event) {
    log.trace("Conversation with id {} cancelled", event.getId());
  }

}
