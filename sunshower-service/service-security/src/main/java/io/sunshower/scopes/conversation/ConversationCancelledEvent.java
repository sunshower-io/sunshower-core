package io.sunshower.scopes.conversation;

public class ConversationCancelledEvent extends ConversationEvent {
  public ConversationCancelledEvent(String id, ConversationState state) {
    super(id, state);
  }

  public ConversationCancelledEvent(ConversationContext conversation) {
    super(conversation);
  }
}
