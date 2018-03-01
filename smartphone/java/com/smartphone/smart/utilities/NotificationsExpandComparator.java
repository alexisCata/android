package com.cathedralsw.schoolparent.utilities;

import com.cathedralsw.schoolparent.classes.SchoolNotification;

import java.util.Comparator;

/**
 * Created by alexis on 28/09/17.
 */

public class NotificationsExpandComparator implements Comparator<SchoolNotification> {

    @Override
    public int compare(SchoolNotification o1, SchoolNotification o2) {
        return o2.getTimestamp().compareTo(o1.getTimestamp());
    }
}