package io.sunshower.scopes.conversation;

import lombok.val;

import static java.lang.String.format;

public enum ConversationState {

    Initiated,
    Cancelled,
    Finalized;


    public static ConversationState fromString(String s) {
        if(s == null) {
            throw new IllegalArgumentException(format("Illegal conversation state: null"));
        }
        val normalized = s.toLowerCase().trim();
        switch (normalized) {
            case "initiated": return Initiated;
            case "cancelled": return Cancelled;
            case "finalized": return Finalized;
        }
        throw new IllegalArgumentException("Illegal conversation state: " + s);
    }

}
