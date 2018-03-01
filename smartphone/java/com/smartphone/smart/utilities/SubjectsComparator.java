package com.cathedralsw.schoolparent.utilities;

import com.cathedralsw.schoolparent.classes.Subject;

import java.util.Comparator;

/**
 * Created by alexis on 28/09/17.
 */

public class SubjectsComparator implements Comparator<Subject> {

    @Override
    public int compare(Subject o1, Subject o2) {
        return o1.getName().compareTo(o2.getName());
    }
}