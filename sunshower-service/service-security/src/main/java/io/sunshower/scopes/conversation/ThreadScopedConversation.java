package io.sunshower.scopes.conversation;

import lombok.val;

public class ThreadScopedConversation implements Conversation {

    @Override
    public String getId() {
        val conversation = ConversationHolder.getConversation();
        if(conversation != null) {
            return conversation.getId();
        }
        return null;
    }

    @Override
    public ConversationState getState() {
        val conversation = ConversationHolder.getConversation();
        if(conversation != null) {
            return conversation.getState();
        }
        return null;
    }
}
