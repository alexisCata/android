package com.cathedralsw.schoolparent.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by alexis on 31/10/17.
 */

public class Class implements Parcelable {
    Integer id;
    String name;

    public Class() {
    }

    public Class(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Class(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
        } catch (Exception e) {
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

    protected Class(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
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
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Class> CREATOR = new Parcelable.Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel in) {
            return new Class(in);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };
}
