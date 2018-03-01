package com.cathedralsw.schoolparent.utilities;

import com.cathedralsw.schoolparent.classes.ChatMessage;

import java.util.Comparator;

/**
 * Created by alexis on 11/10/17.
 */

public class ChatMessageComparator implements Comparator<ChatMessage> {

    @Override
    public int compare(ChatMessage o1, ChatMessage o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}
