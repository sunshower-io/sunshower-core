package io.sunshower.scopes.conversation;

import lombok.Getter;

@Getter
public class ConversationContext implements Conversation {

  final String id;
  final ConversationState state;

  public ConversationContext(String id, ConversationState state) {
    this.id = id;
    this.state = state;
  }
}
