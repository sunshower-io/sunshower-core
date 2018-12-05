package io.sunshower.scopes.conversation;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConversationSerializer {

    ConversationContext parse(String input);
    ConversationContext parse(InputStream input);

    void write(OutputStream os, ConversationContext conversationContext);

    String write(ConversationContext conversationContext);
}
