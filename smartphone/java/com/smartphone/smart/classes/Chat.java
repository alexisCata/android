package com.cathedralsw.schoolparent.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexis on 11/10/17.
 */

public class Chat implements Parcelable {
    private User userTo;
    private User lastUser;
    private String lastMessage;
    private Date timestamp;
    private Date lastRead;

    public Chat(User userTo, User lastUser, String lastMessage, Date timestamp, Date lastRead) {
        this.userTo = userTo;
        this.lastUser = lastUser;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.lastRead = lastRead;
    }

    public Chat(JSONObject object) {
        try {

            this.userTo = new User((JSONObject) object.get("user"));
            this.lastUser = new User((JSONObject) object.get("last_user_from"));
            this.lastMessage = object.getString("last_message");

            SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
            this.timestamp = dateFormat.parse(object.getString("timestamp"));
            this.lastRead = dateFormat.parse(object.getString("last_read"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public User getLastUser() {
        return lastUser;
    }

    public void setLastUser(User lastUser) {
        this.lastUser = lastUser;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getLastRead() {
        return lastRead;
    }

    public void setLastRead(Date lastRead) {
        this.lastRead = lastRead;
    }


    protected Chat(Parcel in) {
        userTo = (User) in.readValue(User.class.getClassLoader());
        lastUser = (User) in.readValue(User.class.getClassLoader());
        lastMessage = in.readString();
        long tmpTimestamp = in.readLong();
        timestamp = tmpTimestamp != -1 ? new Date(tmpTimestamp) : null;
        long tmpLastRead = in.readLong();
        lastRead = tmpLastRead != -1 ? new Date(tmpLastRead) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userTo);
        dest.writeValue(lastUser);
        dest.writeString(lastMessage);
        dest.writeLong(timestamp != null ? timestamp.getTime() : -1L);
        dest.writeLong(lastRead != null ? lastRead.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}