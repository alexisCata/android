package com.cathedralsw.schoolparent.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cathedralsw.schoolparent.classes.Chat;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.classes.Subject;
import com.cathedralsw.schoolparent.classes.User;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alexis on 22/01/18.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "school_parent";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_NOTIFICATIONS_HOME = "notifications_home";
    private static final String TABLE_NOTIFICATIONS_CALENDAR = "notifications_calendar";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SUBJECTS = "subjects";

    // table fields
    private static final String NOTIFICATION = "notification";
    private static final String CHAT = "chat";
    private static final String USER = "user";
    private static final String SUBJECT = "subject";


    private static final String CREATE_TABLE_NOTIFICATIONS_HOME = "CREATE TABLE " +
            TABLE_NOTIFICATIONS_HOME + "(" + NOTIFICATION + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_NOTIFICATIONS_CALENDAR = "CREATE TABLE " +
            TABLE_NOTIFICATIONS_CALENDAR + "(" + NOTIFICATION + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_CHATS = "CREATE TABLE " +
            TABLE_CHATS + "(" + CHAT + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " +
            TABLE_USERS + "(" + CHAT + " TEXT PRIMARY KEY )";

    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE " +
            TABLE_SUBJECTS + "(" + SUBJECT + " TEXT PRIMARY KEY )";


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

    public void deleteNotificationsCalendar() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS_CALENDAR);
    }

    public void deleteChats() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CHATS);
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USERS);
    }

    public void deleteSubjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SUBJECTS);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_HOME);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_CALENDAR);
        db.execSQL(CREATE_TABLE_CHATS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SUBJECTS);

    }

    public Long saveNotificationHome(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(notification);

        ContentValues values = new ContentValues();
        values.put("notification", njson);

        long id = db.insert(TABLE_NOTIFICATIONS_HOME, null, values);

        return id;
    }

    public Long saveNotificationCalendar(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(notification);

        ContentValues values = new ContentValues();
        values.put("notification", njson);

        long id = db.insert(TABLE_NOTIFICATIONS_CALENDAR, null, values);

        return id;
    }

    public Long saveChat(Chat chat) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(chat);

        ContentValues values = new ContentValues();
        values.put("chat", njson);

        long id = db.insert(TABLE_CHATS, null, values);

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

    public Long saveSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson gson = new Gson();

        String njson = gson.toJson(subject);

        ContentValues values = new ContentValues();
        values.put("subject", njson);

        long id = db.insert(TABLE_SUBJECTS, null, values);

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


    public ArrayList<SchoolNotification> getNotificationsCalendar() {

        String selectQuery = "SELECT " + NOTIFICATION + " FROM " + TABLE_NOTIFICATIONS_CALENDAR;

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

    public ArrayList<Subject> getSubjects() {

        String selectQuery = "SELECT " + SUBJECT + " FROM " + TABLE_SUBJECTS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<Subject> subjects = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    Subject subject = gson.fromJson(c.getString(0), Subject.class);
                    subjects.add(subject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return subjects;
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

