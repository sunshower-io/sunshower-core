package io.sunshower.scopes.conversation;

public class ConversationFinalizedEvent extends ConversationEvent {
  public ConversationFinalizedEvent(ConversationContext conversation) {
    super(conversation);
  }
}
