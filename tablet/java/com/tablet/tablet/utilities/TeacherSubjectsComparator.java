package com.cathedralsw.schoolteacher.utilities;

import com.cathedralsw.schoolteacher.classes.Subject;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;

import java.util.Comparator;

/**
 * Created by alexis on 28/09/17.
 */

public class TeacherSubjectsComparator implements Comparator<TeacherSubject> {

//    @Override
//    public int compare(Subject o1, Subject o2) {
//        return o1.getName().compareTo(o2.getName());
//    }

    @Override
    public int compare(TeacherSubject o1, TeacherSubject o2) {
        int i = o1.getName().compareTo(o2.getName());
        if (i!=0) return i;
        return o1.getSubjectClass().getName().compareTo(o2.getSubjectClass().getName());
    }
}