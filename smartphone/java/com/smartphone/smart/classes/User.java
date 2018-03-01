package com.cathedralsw.schoolparent.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexis on 9/10/17.
 */

public class User implements Parcelable {
    private Integer id;
    private String firstName;
    private String lastName;
    private Date avatarTimestamp;

    public User(){

    }
    public User(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public User(Integer id, String firstName, String lastName, Date avatarTimestamp) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarTimestamp = avatarTimestamp;
    }

    public User(JSONObject object) {
        try{
            this.id = object.getInt("id");
            this.firstName = object.getString("first_name");
            this.lastName = object.getString("last_name");
            if (object.has("avatar_timestamp")){
                String timestamp = object.getString("avatar_timestamp");
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
                    this.avatarTimestamp = dateFormat.parse(timestamp);
                }catch (Exception e){
                    e.printStackTrace();
                    this.avatarTimestamp = null;
                }
            }else
                this.avatarTimestamp = null;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getAvatarTimestamp() {
        return avatarTimestamp;
    }

    public void setAvatarTimestamp(Date avatarTimestamp) {
        this.avatarTimestamp = avatarTimestamp;
    }

    @Override
    public String toString() {
        String name;
        if (id == null){
            name = "";
        }else{
            name = this.getFirstName().concat(" ").concat(this.getLastName());
        }
        return name;
    }

    public String nameForList(){
        String name;
        if (id == null){
            name = "";
        }else{
            name = this.getLastName().concat(", ").concat(this.getFirstName());
        }
        return name;
    }

    protected User(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        long tmpAvatarTimestamp = in.readLong();
        avatarTimestamp = tmpAvatarTimestamp != -1 ? new Date(tmpAvatarTimestamp) : null;
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
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeLong(avatarTimestamp != null ? avatarTimestamp.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}