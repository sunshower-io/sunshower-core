package io.sunshower.service.conversation;

import io.sunshower.scopes.conversation.ConversationHolder;
import io.sunshower.scopes.conversation.ConversationSerializer;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Service
@Provider
public class KeyedConversationFilter implements ContainerRequestFilter, ContainerResponseFilter {


    @Inject private ConversationSerializer serializer;

    public static final String CONVERSATION_HEADER_KEY = "ssio-conversation";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        val conversationId = requestContext.getHeaderString(CONVERSATION_HEADER_KEY);
        if(conversationId != null) {
            ConversationHolder.pushConversation(serializer.parse(conversationId));
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

    }
}
