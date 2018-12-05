package io.sunshower.scopes.conversation;

public abstract class ConversationEvent {

    private final ConversationState state;

    protected ConversationEvent(ConversationState state) {
        this.state = state;
    }
}
