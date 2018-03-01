package com.cathedralsw.schoolteacher.utilities;

import com.cathedralsw.schoolteacher.classes.Student;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;

import java.util.Comparator;

/**
 * Created by alexis on 28/09/17.
 */

public class StudentsComparator implements Comparator<Student> {

    @Override
    public int compare(Student o1, Student o2) {
        return o1.nameForList().compareTo(o2.nameForList());

    }
}