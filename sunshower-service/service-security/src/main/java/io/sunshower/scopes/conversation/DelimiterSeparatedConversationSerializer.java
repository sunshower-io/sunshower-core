package io.sunshower.scopes.conversation;

import static java.lang.String.format;

import io.sunshower.io.Files;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.SneakyThrows;
import lombok.val;

/** Expects format id:state */
public class DelimiterSeparatedConversationSerializer implements ConversationSerializer {
  final String delimiter;

  public DelimiterSeparatedConversationSerializer(char delimiter) {
    this.delimiter = Character.toString(delimiter);
  }

  @Override
  public ConversationContext parse(String input) {
    val s = input.trim().split(delimiter);
    if (s.length == 1) {
      return new ConversationContext(null, ConversationState.Initiated);
    }
    if (s.length != 2) {
      throw new IllegalArgumentException(format("Can't extract conversation from: '%s'", input));
    }
    val id = s[0];
    val state = ConversationState.fromString(s[1]);
    if (state == ConversationState.Initiated) {
      throw new IllegalArgumentException("Client cannot initiate a conversation");
    }
    return new ConversationContext(id, state);
  }

  @Override
  @SneakyThrows
  public ConversationContext parse(InputStream input) {
    return parse(new String(Files.read(input), "UTF-8"));
  }

  @Override
  @SneakyThrows
  public void write(OutputStream os, ConversationContext conversationContext) {
    os.write(write(conversationContext).getBytes());
  }

  @Override
  public String write(ConversationContext conversationContext) {
    return conversationContext.id + ":" + conversationContext.getState().name().toLowerCase();
  }
}
