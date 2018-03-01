package com.cathedralsw.schoolteacher.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexis on 28/09/17.
 */

public class SchoolNotification implements Parcelable {

    private Integer id;
    private String type;
    private User owner;
    private String title;
    private String description;
    private Date date;
    private Date timestamp;
    private Student target_student;
    private Class target_class;
    private Subject subject;

    public SchoolNotification(Integer id, String type, User owner, String title, String description, Date date, Date timestamp, Student target_student, Class target_class, Subject subject) {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.date = date;
        this.timestamp = timestamp;
        this.target_student = target_student;
        this.target_class = target_class;
        this.subject = subject;
    }

    public SchoolNotification(JSONObject notif) {
        try {
            this.id = notif.getInt("id");
            this.type = notif.getString("type");
            this.owner = new User((JSONObject) notif.get("owner"));
            this.title = notif.getString("title");
            this.description = notif.getString("description");

            SimpleDateFormat dateFormat = new SimpleDateFormat("y-MM-dd");
            this.date = dateFormat.parse(notif.getString("date").substring(0, 10));
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
            this.timestamp = dateFormat.parse(notif.getString("timestamp"));

            Student student = new Student();
            if (!notif.isNull("target_student")) {
                student = new Student((JSONObject) notif.get("target_student"));
            }

            this.target_student = student;

            Class notifClass = new Class();
            if (!notif.isNull("target_class")) {
                notifClass = new Class((JSONObject) notif.get("target_class"));
            }
            this.target_class = notifClass;

            Subject subject = new Subject();
            if (!notif.isNull("subject")) {
                subject = new Subject((JSONObject) notif.get("subject"));
            }
            this.subject = subject;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Student getTarget_student() {
        return target_student;
    }

    public void setTarget_student(Student target_student) {
        this.target_student = target_student;
    }

    public Class getTarget_class() {
        return target_class;
    }

    public void setTarget_class(Class target_class) {
        this.target_class = target_class;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }


    protected SchoolNotification(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        type = in.readString();
        owner = (User) in.readValue(User.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        long tmpTimestamp = in.readLong();
        timestamp = tmpTimestamp != -1 ? new Date(tmpTimestamp) : null;
        target_student = (Student) in.readValue(Student.class.getClassLoader());
        target_class = (Class) in.readValue(Class.class.getClassLoader());
        subject = (Subject) in.readValue(Subject.class.getClassLoader());
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
        dest.writeString(type);
        dest.writeValue(owner);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeLong(timestamp != null ? timestamp.getTime() : -1L);
        dest.writeValue(target_student);
        dest.writeValue(target_class);
        dest.writeValue(subject);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SchoolNotification> CREATOR = new Parcelable.Creator<SchoolNotification>() {
        @Override
        public SchoolNotification createFromParcel(Parcel in) {
            return new SchoolNotification(in);
        }

        @Override
        public SchoolNotification[] newArray(int size) {
            return new SchoolNotification[size];
        }
    };
}