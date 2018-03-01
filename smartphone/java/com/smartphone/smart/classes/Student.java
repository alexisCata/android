package com.cathedralsw.schoolparent.classes;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alexis on 5/10/17.
 */

public class Student extends User implements Serializable{

    private ArrayList<Subject> subjects;
    private Class studentClass;

    public Student(Integer id, String first_name, String last_name, ArrayList<Subject> subjects, Class studentClass) {
        super(id, first_name, last_name);
        this.subjects = subjects;
        this.studentClass = studentClass;
    }

    public Student(JSONObject object, ArrayList<Subject> subjects, Class studentClass) {
        super(object);
        this.subjects = subjects;
        this.studentClass = studentClass;
    }

    public Class getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(Class studentClass) {
        this.studentClass = studentClass;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
