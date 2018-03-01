package com.cathedralsw.schoolteacher.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cathedralsw.schoolteacher.classes.Chat;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.Subject;
import com.cathedralsw.schoolteacher.classes.User;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by alexis on 22/01/18.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "school_teacher";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_NOTIFICATIONS_HOME = "notifications_home";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String TABLE_SUBJECTS = "subjects";
    private static final String TABLE_NOTIFICATIONS_SUBJECT = "notification_subject";
    private static final String TABLE_STUDENTS_CLASS = "students_class";

    private static final String TABLE_NOTIFICATIONS_TASKS = "notifications_tasks";
    private static final String TABLE_NOTIFICATIONS_EXAMS = "notifications_exams";
    private static final String TABLE_USERS = "users";


    // table fields
    private static final String NOTIFICATION = "notification";
    private static final String CHAT = "chat";
    private static final String SCHEDULE = "schedule";
    private static final String SUBJECT = "subject";
    private static final String NOTIFICATION_SUBJECT_ID = "subject_id";
    private static final String NOTIFICATION_CLASS_ID = "class_id";

    private static final String STUDENT_CLASS_ID = "class_id";
    private static final String STUDENT = "student";

    private static final String USER = "user";


    private static final String CREATE_TABLE_NOTIFICATIONS_HOME = "CREATE TABLE " +
            TABLE_NOTIFICATIONS_HOME + "(" + NOTIFICATION + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_CHATS = "CREATE TABLE " +
            TABLE_CHATS + "(" + CHAT + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE " +
            TABLE_SCHEDULE + "(" + SCHEDULE + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE " +
            TABLE_SUBJECTS + "(" + SUBJECT + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_STUDENTS_CLASS = "CREATE TABLE " +
            TABLE_STUDENTS_CLASS +
            "(" + STUDENT_CLASS_ID + " NUMBER, " +
            STUDENT + " TEXT, " +
            "PRIMARY KEY (" + STUDENT_CLASS_ID + ", " + STUDENT +"))";

    private static final String CREATE_TABLE_NOTIFICATIONS_SUBJECT = "CREATE TABLE " +
            TABLE_NOTIFICATIONS_SUBJECT +
            "(" + NOTIFICATION_SUBJECT_ID + " NUMBER, " +
            NOTIFICATION_CLASS_ID + " NUMBER, " +
            NOTIFICATION + " TEXT, " +
            "PRIMARY KEY (" + NOTIFICATION_SUBJECT_ID + ", " + NOTIFICATION_CLASS_ID + "," + NOTIFICATION +"))";

    private static final String CREATE_TABLE_NOTIFICATIONS_TASKS = "CREATE TABLE " +
            TABLE_NOTIFICATIONS_TASKS + "(" + NOTIFICATION + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_NOTIFICATIONS_EXAMS = "CREATE TABLE " +
            TABLE_NOTIFICATIONS_EXAMS + "(" + NOTIFICATION + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " +
            TABLE_USERS + "(" + CHAT + " TEXT PRIMARY KEY )";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        context.deleteDatabase(DATABASE_NAME);

    }

    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + DATABASE_NAME);

    }

    public void deleteNotificationsHome() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS_HOME);
    }

    public void deleteChats() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CHATS);
    }

    public void deleteSchedule() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SCHEDULE);
    }

    public void deleteSubjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SUBJECTS);
    }

    public void deleteNotificationsSubject(Integer classId, Integer subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS_SUBJECT +
                " WHERE " + NOTIFICATION_CLASS_ID + " = " + classId.toString() +
                " AND " + NOTIFICATION_SUBJECT_ID + " = " + subjectId.toString());
    }

    public void deleteStudentsClass(Integer classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STUDENTS_CLASS +
                " WHERE " + STUDENT_CLASS_ID + " = " + classId.toString());
    }

    public void deleteNotificationsTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS_TASKS);
    }

    public void deleteNotificationsExams() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS_EXAMS);
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USERS);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_HOME);
        db.execSQL(CREATE_TABLE_CHATS);
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_SUBJECTS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_SUBJECT);
        db.execSQL(CREATE_TABLE_STUDENTS_CLASS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_TASKS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_EXAMS);
        db.execSQL(CREATE_TABLE_USERS);


    }

    public Long saveNotificationHome(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(notification);

        ContentValues values = new ContentValues();
        values.put(NOTIFICATION, njson);

        long id = db.insert(TABLE_NOTIFICATIONS_HOME, null, values);

        return id;
    }

    public Long saveChat(Chat chat) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(chat);

        ContentValues values = new ContentValues();
        values.put(CHAT, njson);

        long id = db.insert(TABLE_CHATS, null, values);

        return id;
    }

    public Long saveSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(schedule);

        ContentValues values = new ContentValues();
        values.put(SCHEDULE, njson);

        long id = db.insert(TABLE_SCHEDULE, null, values);

        return id;
    }

    public Long saveSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(subject);

        ContentValues values = new ContentValues();
        values.put(SUBJECT, njson);

        long id = db.insert(TABLE_SUBJECTS, null, values);

        return id;
    }

    public Long saveNotificationSubject(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(notification);

        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_CLASS_ID, notification.getTarget_class().getId());
        values.put(NOTIFICATION_SUBJECT_ID, notification.getSubject().getId());
        values.put(NOTIFICATION, njson);

        long id = db.insert(TABLE_NOTIFICATIONS_SUBJECT, null, values);

        return id;
    }

    public Long saveStudentClass(Student student, Integer classId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(student);

        ContentValues values = new ContentValues();
        values.put(STUDENT_CLASS_ID, classId);
        values.put(STUDENT, njson);

        long id = db.insert(TABLE_STUDENTS_CLASS, null, values);

        return id;
    }

    public Long saveNotificationTaks(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(notification);

        ContentValues values = new ContentValues();
        values.put(NOTIFICATION, njson);

        long id = db.insert(TABLE_NOTIFICATIONS_TASKS, null, values);

        return id;
    }

    public Long saveNotificationExams(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(notification);

        ContentValues values = new ContentValues();
        values.put(NOTIFICATION, njson);

        long id = db.insert(TABLE_NOTIFICATIONS_EXAMS, null, values);

        return id;
    }

    public Long saveUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(user);

        ContentValues values = new ContentValues();
        values.put("user", njson);

        long id = db.insert(TABLE_USERS, null, values);

        return id;
    }



    public ArrayList<SchoolNotification> getNotificationsHome() {

        String selectQuery = "SELECT " + NOTIFICATION + " FROM " + TABLE_NOTIFICATIONS_HOME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<SchoolNotification> notifications = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    SchoolNotification notification = gson.fromJson(c.getString(0), SchoolNotification.class);
                    notifications.add(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return notifications;
    }

    public ArrayList<Chat> getChats() {

        String selectQuery = "SELECT " + CHAT + " FROM " + TABLE_CHATS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<Chat> chats = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    Chat chat = gson.fromJson(c.getString(0), Chat.class);
                    chats.add(chat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return chats;
    }

    public ArrayList<Schedule> getSchedule() {

        String selectQuery = "SELECT " + SCHEDULE + " FROM " + TABLE_SCHEDULE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<Schedule> schedules = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    Schedule schedule = gson.fromJson(c.getString(0), Schedule.class);
                    schedules.add(schedule);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return schedules;
    }

    public ArrayList<TeacherSubject> getSubjects() {

        String selectQuery = "SELECT " + SUBJECT + " FROM " + TABLE_SUBJECTS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<TeacherSubject> subjects = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    TeacherSubject subject = gson.fromJson(c.getString(0), TeacherSubject.class);
                    subjects.add(subject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return subjects;
    }

    public ArrayList<SchoolNotification> getNotificationsSubject(Integer classId, Integer subjectId) {

        String selectQuery = "SELECT " + NOTIFICATION + " FROM " + TABLE_NOTIFICATIONS_SUBJECT +
                " WHERE " + NOTIFICATION_CLASS_ID + " = " + classId.toString() +
                " AND " + NOTIFICATION_SUBJECT_ID + " = " + subjectId.toString();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<SchoolNotification> notifications = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    SchoolNotification notification = gson.fromJson(c.getString(0), SchoolNotification.class);
                    notifications.add(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return notifications;
    }

    public ArrayList<Student> getStudentsClass(Integer classId) {

        String selectQuery = "SELECT " + STUDENT + " FROM " + TABLE_STUDENTS_CLASS +
                " WHERE " + STUDENT_CLASS_ID + " = " + classId.toString();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<Student> students = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    Student student = gson.fromJson(c.getString(0), Student.class);
                    students.add(student);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return students;
    }

    public ArrayList<SchoolNotification> getNotificationsTasks() {

        String selectQuery = "SELECT " + NOTIFICATION + " FROM " + TABLE_NOTIFICATIONS_TASKS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<SchoolNotification> notifications = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    SchoolNotification notification = gson.fromJson(c.getString(0), SchoolNotification.class);
                    notifications.add(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return notifications;
    }

    public ArrayList<SchoolNotification> getNotificationsExams() {

        String selectQuery = "SELECT " + NOTIFICATION + " FROM " + TABLE_NOTIFICATIONS_EXAMS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<SchoolNotification> notifications = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    SchoolNotification notification = gson.fromJson(c.getString(0), SchoolNotification.class);
                    notifications.add(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return notifications;
    }

    public ArrayList<User> getUsers() {

        String selectQuery = "SELECT " + USER + " FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<User> users = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    User user = gson.fromJson(c.getString(0), User.class);
                    users.add(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return users;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}

