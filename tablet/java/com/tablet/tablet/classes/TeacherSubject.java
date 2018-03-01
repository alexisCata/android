package com.cathedralsw.schoolteacher.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by alexis on 3/11/17.
 */

public class TeacherSubject extends Subject implements Parcelable {

    private Class subjectClass;

    public TeacherSubject(Class subjectClass) {
        this.subjectClass = subjectClass;
    }

    public TeacherSubject(Integer id, String name, Class subjectClass) {
        super(id, name);
        this.subjectClass = subjectClass;
    }

    public TeacherSubject(JSONObject object, Class subjectClass) {
        super(object);
        this.subjectClass = subjectClass;
    }

    public TeacherSubject(Parcel in, Class subjectClass) {
        super(in);
        this.subjectClass = subjectClass;
    }

    public Class getSubjectClass() {
        return subjectClass;
    }

    public void setSubjectClass(Class subjectClass) {
        this.subjectClass = subjectClass;
    }

    protected TeacherSubject(Parcel in) {
        subjectClass = (Class) in.readValue(Class.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(subjectClass);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TeacherSubject> CREATOR = new Parcelable.Creator<TeacherSubject>() {
        @Override
        public TeacherSubject createFromParcel(Parcel in) {
            return new TeacherSubject(in);
        }

        @Override
        public TeacherSubject[] newArray(int size) {
            return new TeacherSubject[size];
        }
    };
}