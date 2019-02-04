package io.sunshower.scopes.conversation;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.AbstractDynamicScope;
import io.sunshower.security.events.LogoutEvent;
import io.sunshower.service.security.Session;
import java.util.concurrent.TimeUnit;
import javax.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;

@Slf4j
public class ConversationScope extends AbstractDynamicScope<String>
    implements Scope, DisposableBean, ApplicationListener<LogoutEvent> {

  private final Provider<Conversation> conversationProvider;

  public ConversationScope(
      Provider<Conversation> conversation,
      Provider<Cache> cache,
      Provider<Session> session,
      Provider<KeyProvider> keyProvider) {
    super(cache, session, keyProvider);
    this.conversationProvider = conversation;
  }

  protected Conversation conversation() {
    return conversationProvider.get();
  }

  @Override
  protected int getMaxSize() {
    return 10;
  }

  @Override
  protected String getId() {
    return conversation().getId();
  }

  @Override
  protected String cacheKey(Session session) {
    return String.format("%s:%s", keyProvider().getKey(), "conversation-scope");
  }

  @EventListener(ConversationInitiatedEvent.class)
  public void onConversationInitiated(ConversationInitiatedEvent event) {
    log.trace("Conversation with id {} initialized", event.getId());
    //        createScopeRegion(event.getId());
  }

  @EventListener(ConversationFinalizedEvent.class)
  public void onConversationFinalized(ConversationFinalizedEvent event) {
    log.trace("Conversation with id {} finalized", event.getId());
  }

  @EventListener(ConversationCancelledEvent.class)
  public void onConversationCancelled(ConversationFinalizedEvent event) {
    log.trace("Conversation with id {} cancelled", event.getId());
  }

  protected long getTimeoutMillis() {
    return TimeUnit.MINUTES.toMillis(60);
  }
}
