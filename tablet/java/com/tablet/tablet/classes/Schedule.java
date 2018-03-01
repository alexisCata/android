package com.cathedralsw.schoolteacher.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.sql.Time;

/**
 * Created by alexis on 10/11/17.
 */

public class Schedule implements Parcelable {
    private String day;
    //    private Time time;
    private String time;
    private Integer order;
    //    private Class in_class;
//    private Subject subject;
    private TeacherSubject teacherSubject;

    public Schedule(String day, String time, Integer order, TeacherSubject teacherSubject) {
        this.day = day;
        this.time = time;
        this.order = order;
        this.teacherSubject = teacherSubject;
    }

    public Schedule(JSONObject object, TeacherSubject teacherSubject) {
        try {
            this.day = object.getString("day");
            this.time = object.getString("time").substring(0, 5);
            this.order = object.getInt("order");
        }catch (Exception e){
            e.printStackTrace();
        }
        this.teacherSubject = teacherSubject;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public TeacherSubject getTeacherSubject() {
        return teacherSubject;
    }

    public void setTeacherSubject(TeacherSubject teacherSubject) {
        this.teacherSubject = teacherSubject;
    }

    protected Schedule(Parcel in) {
        day = in.readString();
        time = in.readString();
        order = in.readByte() == 0x00 ? null : in.readInt();
        teacherSubject = (TeacherSubject) in.readValue(TeacherSubject.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(day);
        dest.writeString(time);
        if (order == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(order);
        }
        dest.writeValue(teacherSubject);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}