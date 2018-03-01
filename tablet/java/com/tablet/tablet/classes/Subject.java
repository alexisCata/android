package com.cathedralsw.schoolteacher.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by alexis on 9/10/17.
 */

public class Subject implements Parcelable {
    private Integer id;
    private String name;
    private Float score_avg;
    private Float score_avg_period;
    private Integer tasks;

    public Subject() {
        this.name = "";
    }

    public Subject(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Subject(JSONObject object) {
        try{
            this.id = object.getInt("id");
            this.name = object.getString("name");
            if (object.has("score_avg") && !object.isNull("score_avg"))
                this.score_avg = Float.parseFloat(object.getString("score_avg"));
            if (object.has("score_avg_period") && !object.isNull("score_avg_period"))
                this.score_avg_period = Float.parseFloat(object.getString("score_avg_period"));
            if (object.has("tasks") && !object.isNull("tasks"))
                this.tasks = object.getInt("tasks");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getScore_avg() {
        return score_avg;
    }

    public void setScore_avg(Float score_avg) {
        this.score_avg = score_avg;
    }

    public Float getScore_avg_period() {
        return score_avg_period;
    }

    public void setScore_avg_period(Float score_avg_period) {
        this.score_avg_period = score_avg_period;
    }

    public Integer getTasks() {
        return tasks;
    }

    public void setTasks(Integer tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return name == null ? "" : name;
    }



    protected Subject(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        score_avg = in.readByte() == 0x00 ? null : in.readFloat();
        score_avg_period = in.readByte() == 0x00 ? null : in.readFloat();
        tasks = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (score_avg == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(score_avg);
        }
        if (score_avg_period == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(score_avg_period);
        }
        if (tasks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(tasks);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };
}