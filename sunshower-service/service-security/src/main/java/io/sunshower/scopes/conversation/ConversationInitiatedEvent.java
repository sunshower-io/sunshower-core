package io.sunshower.scopes.conversation;

public class ConversationInitiatedEvent extends ConversationEvent {
  public ConversationInitiatedEvent(Conversation conversation) {
    super(conversation);
  }

  public ConversationInitiatedEvent(String id) {
    super(id, ConversationState.Initiated);
  }
}
