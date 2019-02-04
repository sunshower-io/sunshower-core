package io.sunshower.scopes.conversation;

import lombok.val;

public class ConversationHolder {

  static final ThreadLocal<Conversation> conversationHolder = new InheritableThreadLocal<>();

  public static Conversation pushConversation(Conversation conversation) {
    val result = conversationHolder.get();
    conversationHolder.set(conversation);
    return result;
  }

  public static Conversation popConversation() {
    val cvs = conversationHolder.get();
    conversationHolder.set(null);
    return cvs;
  }

  public static Conversation getConversation() {
    return conversationHolder.get();
  }
}
