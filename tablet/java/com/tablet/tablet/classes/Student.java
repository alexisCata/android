package com.cathedralsw.schoolteacher.classes;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alexis on 5/10/17.
 */

public class Student extends User implements Serializable{

//    private ArrayList<Subject> subjects;

    public Student() {
    }

    public Student(Integer id, String first_name, String last_name){//, ArrayList<Subject> subjects) {
        super(id, first_name, last_name);
//        this.subjects = subjects;
    }

    public Student(JSONObject object){//, ArrayList<Subject> subjects) {
        super(object);
//        this.subjects = subjects;
    }

//    public ArrayList<Subject> getSubjects() {
//        return subjects;
//    }
//
//    public void setSubjects(ArrayList<Subject> subjects) {
//        this.subjects = subjects;
//    }

    @Override
    public String toString() {
        return super.toString();
    }
}
