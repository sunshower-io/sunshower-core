package io.sunshower.service.conversation;

import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.scopes.conversation.*;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
@Provider
public class KeyedConversationFilter implements ContainerRequestFilter, ApplicationContextAware {

  private ApplicationContext context;
  @Inject private KeyProvider keyProvider;
  @Inject private ConversationSerializer serializer;

  public static final String CONVERSATION_HEADER_KEY = "ssio-conversation";

  @Override
  public void filter(ContainerRequestContext requestContext) {
    val conversationId = requestContext.getHeaderString(CONVERSATION_HEADER_KEY);
    if (conversationId != null) {
      publish(conversationId);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }

  private void publish(String conversationId) {
    val conversation = serializer.parse(conversationId);
    switch (conversation.getState()) {
      case Initiated:
        context.publishEvent(new ConversationInitiatedEvent(keyProvider.secureString(10)));
        break;
      case Cancelled:
        context.publishEvent(new ConversationCancelledEvent(conversation));
        break;
      case Finalized:
        context.publishEvent(new ConversationFinalizedEvent(conversation));
        break;
    }
  }
}
