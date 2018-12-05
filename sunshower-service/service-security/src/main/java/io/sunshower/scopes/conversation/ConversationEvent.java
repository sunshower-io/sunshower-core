package io.sunshower.scopes.conversation;

import lombok.Getter;

@Getter
public abstract class ConversationEvent {

  private final String id;
  private final ConversationState state;

  protected ConversationEvent(Conversation conversation) {
    this(conversation.getId(), conversation.getState());
  }

  protected ConversationEvent(String id, ConversationState state) {
    this.id = id;
    this.state = state;
  }
}
