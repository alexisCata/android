package com.cathedralsw.schoolteacher.utilities;

import com.cathedralsw.schoolteacher.classes.Chat;

import java.util.Comparator;

/**
 * Created by alexis on 11/10/17.
 */

public class ChatsComparator implements Comparator<Chat> {

    @Override
    public int compare(Chat o1, Chat o2) {
        return o2.getTimestamp().compareTo(o1.getTimestamp());
    }
}
