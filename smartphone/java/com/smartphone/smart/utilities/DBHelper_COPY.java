package com.cathedralsw.schoolparent.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cathedralsw.schoolparent.classes.Chat;
import com.cathedralsw.schoolparent.classes.Class;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.classes.Student;
import com.cathedralsw.schoolparent.classes.Subject;
import com.cathedralsw.schoolparent.classes.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alexis on 22/01/18.
 */

public class DBHelper_COPY extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "school";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_SUBJECTS = "subjects";

    // Columns table notifications
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String OWNER_ID = "owner_id";
    private static final String OWNER_FIRSTNAME = "owner_firstname";
    private static final String OWNER_LASTNAME = "owner_lastname";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String DATE = "date";
    private static final String TIMESTAMP = "timestamp";
    private static final String TARGET_STUDENT_ID = "target_student_id";
    private static final String STUDENT_FIRSTNAME = "student_firstname";
    private static final String STUDENT_LASTNAME = "student_lastname";
    private static final String TARGET_CLASS_ID = "target_class_id";
    private static final String CLASS_NAME = "class_name";
    private static final String SUBJECT_ID = "subject_id";
    private static final String SUBJECT_NAME = "subject_name";

    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE "
            + TABLE_NOTIFICATIONS
            + "(" + ID + " INTEGER PRIMARY KEY,"
            + TYPE + " TEXT,"
            + OWNER_ID + " INTEGER,"
            + TITLE + " TEXT,"
            + DESCRIPTION + " TEXT,"
            + DATE + " NUMERIC,"
            + TIMESTAMP + " NUMERIC,"
            + TARGET_STUDENT_ID + " INTEGER,"
            + TARGET_CLASS_ID + " INTEGER,"
            + SUBJECT_ID + " INTEGER)";

    // Columns table users
//    private static final String ID = "id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String AVATAR_TIMESTAMP = "avatar_timestamp";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS
            + "(" + ID + " INTEGER PRIMARY KEY,"
            + FIRST_NAME + " TEXT,"
            + LAST_NAME + " TEXT,"
            + AVATAR_TIMESTAMP + " TEXT)";

    // Columns table classes
//    private static final String ID = "id";
    private static final String NAME = "name";

    private static final String CREATE_TABLE_CLASSES = "CREATE TABLE "
            + TABLE_CLASSES
            + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT)";


    // Columns table subjects
//    private static final String ID = "id";
//    private static final String NAME = "name";

    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE "
            + TABLE_SUBJECTS
            + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT)";

    // Columns table chats
    private static final String USER = "user";
    private static final String LAST_USER_FROM = "last_user_from";
    private static final String LAST_MESSAGE = "last_message";
    //    private static final String TIMESTAMP = "timestamp";
    private static final String LAST_READ = "last_read";

    private static final String CREATE_TABLE_CHATS = "CREATE TABLE "
            + TABLE_CHATS
            + "(" + USER + " INTEGER,"
            + LAST_USER_FROM + " INTEGER,"
            + LAST_MESSAGE + " TEXT,"
            + TIMESTAMP + " TEXT,"
            + LAST_READ + " TEXT," +
            "PRIMARY KEY ("+ USER + "))";

    // Columns table students
    private static final String USER_ID = "user_id";
    private static final String CLASS_ID = "class_id";

    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE "
            + TABLE_STUDENTS
            + "(" + USER_ID + " INTEGER,"
            + CLASS_ID + " INTEGER,"
            + SUBJECT_ID + " INTEGER,"
            + " PRIMARY KEY (" + USER_ID + ", " + CLASS_ID + ", " + SUBJECT_ID + "))";


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");


    public DBHelper_COPY(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void deleteDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + DATABASE_NAME);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_NOTIFICATIONS);
        db.execSQL(CREATE_TABLE_CHATS);
        db.execSQL(CREATE_TABLE_CLASSES);
        db.execSQL(CREATE_TABLE_SUBJECTS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_STUDENTS);

    }

    public Long saveNotification(SchoolNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, notification.getId());
        values.put(TYPE, notification.getType());
        values.put(OWNER_ID, notification.getOwner().getId());
        values.put(TITLE, notification.getTitle());
        values.put(DESCRIPTION, notification.getDescription());
        values.put(DATE, notification.getDate().getTime());
        values.put(TIMESTAMP, notification.getTimestamp().getTime());
        values.put(TARGET_STUDENT_ID, notification.getTarget_student().getId());
        values.put(TARGET_CLASS_ID, notification.getTarget_class().getId());
        values.put(SUBJECT_ID, notification.getSubject().getId());

        long id = db.replace(TABLE_NOTIFICATIONS, null, values);

        return id;
    }

    public Long saveUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, user.getId());
        values.put(FIRST_NAME, user.getFirstName());
        values.put(LAST_NAME, user.getLastName());
        values.put(AVATAR_TIMESTAMP, user.getAvatarTimestamp() != null ? dateFormat.format(user.getAvatarTimestamp()) : "");

        long id = db.replace(TABLE_USERS, null, values);

        return id;
    }

    public Long saveStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values;
        long id = 0;

        for (Subject s : student.getSubjects()) {
            values = new ContentValues();
            values.put(USER_ID, student.getId());
            values.put(CLASS_ID, student.getStudentClass().getId());
            values.put(SUBJECT_ID, s.getId());

            id = db.replace(TABLE_STUDENTS, null, values);
        }

        return id;
    }

    public Long saveClass(Class c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, c.getId());
        values.put(NAME, c.getName());

        long id = db.replace(TABLE_CLASSES, null, values);

        return id;
    }

    public Long saveSubject(Subject s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, s.getId());
        values.put(NAME, s.getName());

        long id = db.replace(TABLE_SUBJECTS, null, values);

        return id;
    }

    public Long saveChat(Chat c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER, c.getUserTo().getId());
        values.put(LAST_USER_FROM, c.getLastUser().getId());
        values.put(LAST_MESSAGE, c.getLastMessage());
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
        String timestamp = null;
        String lastRead = null;
        try{
            timestamp = dateFormat.format(c.getTimestamp());
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            lastRead = dateFormat.format(c.getLastRead());
        }catch (Exception e){
            e.printStackTrace();
        }
        values.put(TIMESTAMP, timestamp);
        values.put(LAST_READ, lastRead);


        long id = db.replace(TABLE_CHATS, null, values);

        return id;
    }


    public ArrayList<SchoolNotification> getNotifs(Integer studentId, Integer classId) {

        String selectQuery = "SELECT N.ID, N.TYPE, O.ID, O.FIRST_NAME, O.LAST_NAME, N.TITLE, " +
                "N.DESCRIPTION, N.DATE, N.TIMESTAMP, ST.ID, ST.FIRST_NAME, ST.LAST_NAME, " +
                "C.ID, C.NAME, S.ID, S.NAME FROM " + TABLE_NOTIFICATIONS + " AS N " +
                "LEFT JOIN " + TABLE_USERS + " AS O ON N.OWNER_ID = O.ID " +
                "LEFT JOIN " + TABLE_USERS + " AS ST ON N.TARGET_STUDENT_ID = ST.ID " +
                "LEFT JOIN " + TABLE_CLASSES + " AS C ON N.TARGET_CLASS_ID = C.ID " +
                "LEFT JOIN " + TABLE_SUBJECTS + " AS S ON N.SUBJECT_ID = S.ID " +
                "WHERE N.TARGET_STUDENT_ID = " + studentId.toString() + " OR " +
                "N.TARGET_CLASS_ID = " + classId.toString() + " " +
                "ORDER BY N.TIMESTAMP DESC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
        ArrayList<SchoolNotification> notifications = new ArrayList<>();
        SchoolNotification notif;
        User owner;
        User student;
        Class clas;
        Subject subject;
        if (c.moveToFirst()) {
            do {
                subject = new Subject(c.getInt(14), c.getString(15));
                clas = new Class(c.getInt(12), c.getString(13));
                owner = new User(c.getInt(2), c.getString(3), c.getString(4));
                student = new User(c.getInt(9), c.getString(10), c.getString(11));

                Date date = new Date();
                Date timestamp = new Date();
                try {
                    date = new Date(c.getLong(7));
                    timestamp = new Date(c.getLong(8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notif = new SchoolNotification(c.getLong(0), c.getString(1), owner, c.getString(5),
                        c.getString(6), date, timestamp, student, clas, subject);
                notifications.add(notif);

            } while (c.moveToNext());
        }
        return notifications;
    }

    public HashMap<Integer, Student> getStudents(){
        HashMap<Integer, Student> students = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String studentsQuery = "SELECT DISTINCT USER_ID FROM " + TABLE_STUDENTS;
        Cursor c = db.rawQuery(studentsQuery, null);
        ArrayList<Integer> student_ids = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                student_ids.add(c.getInt(0));
            } while (c.moveToNext());
        }

        String selectQuery;

        for (Integer student_id : student_ids){
            selectQuery = "SELECT * FROM " + TABLE_STUDENTS + " AS ST, " +
                    TABLE_USERS  + " AS U, " + TABLE_CLASSES + " AS C, " + TABLE_SUBJECTS  + " AS S " +
                    " WHERE USER_ID = " + student_id +
                    "   and ST.USER_ID = U.ID" +
                    "   AND ST.CLASS_ID = C.ID" +
                    "   AND ST.SUBJECT_ID = S.ID";
            c = db.rawQuery(selectQuery, null);

            ArrayList<Subject> subjects = new ArrayList<>();
            User user;
            Subject subject;
            Class clas;
            Student student;
            if (c.moveToFirst()) {
                try{
                    user = new User(c.getInt(3), c.getString(4), c.getString(5),
                            dateFormat.parse(c.getString(6)));
                }catch (Exception e){
                    e.printStackTrace();
                    user = new User(c.getInt(3), c.getString(4), c.getString(5));
                }
                clas = new Class(c.getInt(7), c.getString(8));
                do {
                    subject = new Subject(c.getInt(9), c.getString(10));
                    subjects.add(subject);
                } while (c.moveToNext());

                student = new Student(user.getId(), user.getFirstName(), user.getLastName(), subjects, clas);

                students.put(student.getId(), student);
            }
        }
        return students;
    }

    public ArrayList<User> getUsers(User parent){

        ArrayList<User> users = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);
        User user;
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) != parent.getId()){
                    try{
                        user = new User(c.getInt(0), c.getString(1), c.getString(2),
                                dateFormat.parse(c.getString(3)));
                    }catch (Exception e){
                        e.printStackTrace();
                        user = new User(c.getInt(0), c.getString(1), c.getString(2));
                    }
                    users.add(user);
                }

            } while (c.moveToNext());
        }

        return users;
    }

    public ArrayList<Chat> getChats(){
        ArrayList<Chat> chats = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_CHATS + " AS C, " + TABLE_USERS + " AS U1," + TABLE_USERS + " AS U2" +
                " WHERE C.USER = U1.ID" +
                "   AND C.LAST_USER_FROM = U2.ID";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);
        User user;
        User lastUser;
        Chat chat;
        if (c.moveToFirst()) {
            do {
                try{
                    user = new User(c.getInt(5), c.getString(6), c.getString(7),
                            dateFormat.parse(c.getString(8)));
                }catch (Exception e){
                    e.printStackTrace();
                    user = new User(c.getInt(5), c.getString(6), c.getString(7));
                }
                try{
                    lastUser = new User(c.getInt(9), c.getString(10), c.getString(11),
                            dateFormat.parse(c.getString(12)));
                }catch (Exception e){
                    e.printStackTrace();
                    lastUser = new User(c.getInt(9), c.getString(10), c.getString(11));
                }
                SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
                Date timestamp = null;
                Date lastRead = null;
                try{
                    timestamp = dateFormat.parse(c.getString(3));
                    lastRead = dateFormat.parse(c.getString(4));
                }catch (Exception e){
                    e.printStackTrace();
                }
                chat = new Chat(user, lastUser, c.getString(2), timestamp, lastRead);

                chats.add(chat);

            } while (c.moveToNext());
        }
        return chats;
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

