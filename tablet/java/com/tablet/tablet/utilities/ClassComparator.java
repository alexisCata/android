package com.cathedralsw.schoolteacher.utilities;

import com.cathedralsw.schoolteacher.classes.Class;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;

import java.util.Comparator;

/**
 * Created by alexis on 6/11/17.
 */

public class ClassComparator implements Comparator<Class> {

    @Override
    public int compare(Class o1, Class o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
