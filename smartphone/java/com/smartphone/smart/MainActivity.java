package com.cathedralsw.schoolparent;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedralsw.schoolparent.classes.Class;
import com.cathedralsw.schoolparent.classes.IClickProfile;
import com.cathedralsw.schoolparent.classes.ISelectedStudent;
import com.cathedralsw.schoolparent.classes.MyFirebaseInstanceIDService;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.classes.Student;
import com.cathedralsw.schoolparent.classes.Subject;
import com.cathedralsw.schoolparent.classes.User;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;
import com.cathedralsw.schoolparent.screens.CalendarFragment;
import com.cathedralsw.schoolparent.screens.ChatFragment;
import com.cathedralsw.schoolparent.screens.ChatsFragment;
import com.cathedralsw.schoolparent.screens.ClickProfileFragment;
import com.cathedralsw.schoolparent.screens.HomeFragment;
import com.cathedralsw.schoolparent.screens.SelectChildFragment;
import com.cathedralsw.schoolparent.screens.SubjectsFragment;
import com.cathedralsw.schoolparent.screens.TasksFragment;
//import com.cathedralsw.schoolparent.utilities.DBHelper;
import com.cathedralsw.schoolparent.utilities.NetworkUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.QBadgeView;

import static com.cathedralsw.schoolparent.utilities.Utils.getJsonToken;


public class MainActivity extends AppCompatActivity implements ISelectedStudent, IClickProfile {

    public final static Integer GALLERY = StaticConfiguration.GALLERY;
    public static final Integer STUDENT = StaticConfiguration.STUDENT;
    public static final Integer IMAGE = StaticConfiguration.IMAGE;
    private final String WEBSOCKET_URL = StaticConfiguration.WEBSOCKET_URL;
    private final String HOME_TAG = StaticConfiguration.HOME_TAG;
    private final String CHAT_TAG = StaticConfiguration.CHAT_TAG;
    private final Integer CAMERA = StaticConfiguration.CAMERA;
    private final String IMAGE_DIRECTORY = StaticConfiguration.IMAGE_DIRECTORY;
    private final String PROFILE_IMG = StaticConfiguration.PROFILE_IMG;
    public boolean newNotifications = false;
    private Integer userId;
    private Integer studentId;
    private Integer classId;
    private HashMap<Integer, Student> students = new HashMap<>();
    private ArrayList<User> users = new ArrayList();
    private Bundle myBundle = new Bundle();
    private Toolbar mainToolBar;
    private CircleImageView profilePhoto;
    private BottomNavigationView bottomNavigation;
    private TextView studentName;
    private ProgressBar progressBar;
    private JSONObject token;
    private String androidId;
    private String firebaseToken;
    private WebSocketClient mWebSocketClient;
    private QBadgeView badge;

    public WebSocketClient getWebSocketClient() {
        return mWebSocketClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // FIXING ORIENTATION
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (!loginSaved()) {
            launchLogin();
        } else {
            getStoredData();
            new initTask(getJsonToken(getSharedPreferences("creds", MODE_PRIVATE))).execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void saveData() {

//        schoolDB.deleteDB();

//        long id;
//        for (SchoolNotification n : notifications) {
//            id = schoolDB.saveNotification(n);
//        }
//
//        for (User u : users) {
//            id = schoolDB.saveUser(u);
//        }
//        id = schoolDB.saveUser(getUser());
//
//        HashMap<Integer, Class> classes = new HashMap<>();
//        HashMap<Integer, Subject> subjects = new HashMap<>();
//
//        for (Student s : students.values()) {
//            id = schoolDB.saveStudent(s);
//            classes.put(s.getStudentClass().getId(), s.getStudentClass());
//            for (Subject sub : s.getSubjects()) {
//                subjects.put(sub.getId(), sub);
//            }
//        }
//
//        for (Class c : classes.values()) {
//            id = schoolDB.saveClass(c);
//        }
//
//        for (Subject sub : subjects.values()) {
//            id = schoolDB.saveSubject(sub);
//        }
//
//        for (Chat c : chats) {
//            id = schoolDB.saveChat(c);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                logOut();
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startHome() {

//        new loadHomeTask().execute();


        mainToolBar = (Toolbar) findViewById(R.id.toolbar);
        profilePhoto = (CircleImageView) findViewById(R.id.student_photo);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);
//            progressBar.getIndeterminateDrawable()
//                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        studentName = (TextView) findViewById(R.id.tv_student_name);

//        View a = (View) bottomNavigation.findViewById(R.id.action_chats);
//        badge = new QBadgeView(getApplicationContext());
//        badge.bindTarget(a);
//        badge.setBadgeBackgroundColor(R.color.primary_dark);
////        badge.setGravityOffset(40, 5, true);
//
//        badge.setGravityOffset(0, 0, true);

        setSupportActionBar(mainToolBar);

        getProfileImage();

        setListeners();

        new startRequestTask(token).execute();

    }

    private void getStoredData() {
        getUserId();
        getStudentId();

//        if (schoolDB == null)
//            schoolDB = new DBHelper(getApplicationContext());
//        if (studentId != null) {
//            notifications = schoolDB.getNotifs(studentId, classId);
//            Collections.sort(notifications, new NotificationsComparatorTimestamp());
//        }
//        if (students.size() != 0)
//            students = schoolDB.getStudents();
//        if (users.size() != 0)
//            users = schoolDB.getUsers(getUser());
//        if (chats.size() != 0) {
//            chats = schoolDB.getChats();
//            Collections.sort(chats, new ChatsComparator());
//        }
    }

    private void setListeners() {
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (students.size() > 1) {

                    ClickProfileFragment dialog = ClickProfileFragment.newInstance();
                    dialog.show(MainActivity.this.getSupportFragmentManager(), "ClickProfileFragment");

                } else {
                    choosePhotoFromGallary();
                }
            }
        });


    }

//    private void showPictureDialog(){
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
//        String[] pictureDialogItems = {
//                getResources().getString(R.string.gallery_photo),
//                getResources().getString(R.string.camera_photo)};
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                choosePhotoFromGallary();
//                                break;
//                            case 1:
//                                takePhotoFromCamera();
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

//    private void takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    new saveProfileImageTask(token, path).execute();
                    profilePhoto.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }
//        else if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            profilePhoto.setImageBitmap(thumbnail);
//            saveImage(thumbnail);
//            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//        }
    }

    public String saveImage(Bitmap myBitmap) {

        Bitmap resizedImage = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 10, myBitmap.getHeight() / 10, true);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

        try {
            File f = new File(getApplicationContext().getFilesDir(), studentId.toString());
//            File f = new File(wallpaperDirectory, studentId.toString());
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
//            MediaScannerConnection.scanFile(this,
//                    new String[]{f.getPath()},
//                    new String[]{"image/png"}, null);
            fo.close();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void loadHome() {
//        bottomNavigation.getMenu().getItem(0).setChecked(true);
//        myBundle.putParcelableArrayList("notifications", notifications);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = HomeFragment.newInstance();
        homeFragment.setArguments(myBundle);
        transaction.replace(R.id.frame_layout, homeFragment, HOME_TAG);
        transaction.commit();
    }

    private boolean loginSaved() {
        token = getJsonToken(getSharedPreferences("creds", MODE_PRIVATE));
        return token.has("token") ? true : false;
    }

    private void logOut() {
        SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        sharedPref = getSharedPreferences("student", MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private String getUserName() {
        SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
        String parent_name = sharedPref.getString("first_name", "").concat(" ").concat(sharedPref.getString("last_name", ""));
        return parent_name;
    }

    private User getUser() {
        SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
        User parent = new User(sharedPref.getInt("userId", 0), sharedPref.getString("first_name", ""), sharedPref.getString("last_name", ""));
        return parent;
    }

    private User getUserById(Integer id) {
        User user = null;
        for (User u : users) {
            if (u.getId() == id) {
                user = u;
                break;
            }
        }
        return user;
    }

    public void saveStudent() {
        SharedPreferences sharedPref = getSharedPreferences("student", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("studentId", studentId);
        editor.putInt("class_id", classId);
        editor.commit();
    }

    public Integer getStudentId() {
        SharedPreferences sharedPref = getSharedPreferences("student", MODE_PRIVATE);
        Integer id = sharedPref.getInt("studentId", 0);
        if (id > 0) {
            studentId = id;
            classId = sharedPref.getInt("class_id", 0);
        }
        return id;
    }

    public Integer getUserId() {
        SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
        Integer id = sharedPref.getInt("userId", 0);
        if (id > 0) {
            userId = id;
        }
        return id;
    }

    public JSONObject getToken() {
        return token;
    }

    @Override
    public void onSelectedStudent(Integer id, Integer class_id) {
        studentId = id;
        classId = class_id;
        saveStudent();
        loadHome();

        studentName.setText(students.get(studentId).toString());
        bottomNavigation.getMenu().getItem(0).setChecked(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private ChatFragment getChatFragment() {
        ChatFragment activeChat = (ChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_TAG);
        return activeChat;
    }

    private void connectWebSocket() {
        URI uri;
        try {
            String url = String.format(WEBSOCKET_URL, token.getString("token"), androidId, firebaseToken);
            uri = new URI(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json_object = new JSONObject(message);
                            if (json_object.has("chat_message")) {
                                ChatFragment activeChat = null;
                                try {
                                    activeChat = getChatFragment();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JSONObject json_message = json_object.getJSONObject("chat_message");
                                if (activeChat != null && activeChat.getUserTo().getId() == json_message.getInt("user_from")) {
                                    activeChat.addMessage(json_message);
                                    JSONObject response = new JSONObject();
                                    JSONObject id = new JSONObject();
                                    id.put("userId", json_message.getString("userId"));
                                    response.put("chat_read", id);
                                    mWebSocketClient.send(response.toString());

                                } else {
                                    User user = getUserById(json_message.getInt("user_from"));
                                    sendNotification(json_message, user == null ? json_message.getString("user_from") : user.toString());
                                }

                            } else if (json_object.has("notification")) {
                                JSONObject json_message = json_object.getJSONObject("notification");
                                sendNotification(json_message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendNotification(JSONObject notification) {
        try {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(notification.getString("date").substring(0, 10) + " / " + notification.getString("title"))
                            .setContentText(notification.getString("description"))
                            .setPriority(android.app.Notification.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setDefaults(android.app.Notification.DEFAULT_ALL);

            mBuilder.setContentIntent(setNotificationAction());

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(createID(), mBuilder.build());

            if (notification.getString("target_student_id").equals(studentId.toString()))
                newNotifications();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(JSONObject notification, String userFrom) {
        try {
            //TODO icon arrow
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(userFrom.toString())
                    .setContentText(notification.getString("message"))
                    .setPriority(android.app.Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(android.app.Notification.DEFAULT_ALL);

            mBuilder.setContentIntent(setNotificationAction());

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(createID(), mBuilder.build());

//            addOneBadge();

            loadChats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent setNotificationAction() {
        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return resultPendingIntent;
    }

    private int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    public void loadNotifications() {
        setAreNewNotifications(false);
        loadHome();
    }

    public void newNotifications() {
        HomeFragment home = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
        if (home != null)
            home.visibilityNewNotifications(true);
        setAreNewNotifications(true);
    }

    public Boolean getAreNewNotifications() {
        return newNotifications;
    }

    public void setAreNewNotifications(Boolean newNotifs) {
        newNotifications = newNotifs;
    }

    public void loadChats() {
        ChatsFragment activeChatsFragment = (ChatsFragment) getSupportFragmentManager().findFragmentByTag(CHAT_TAG);
        Boolean chatsActive = activeChatsFragment != null && activeChatsFragment.isVisible() ? true : false;
        if (chatsActive) {
            activeChatsFragment.requestNewChats();
        }
    }

    @Override
    public void onSelectedOption(Integer option) {
        if (option == STUDENT) {
            ArrayList<Student> studentsToDialog = new ArrayList<>(students.values());
            SelectChildFragment dialog = SelectChildFragment.newInstance(studentsToDialog, studentId);
            dialog.show(MainActivity.this.getSupportFragmentManager(), "SelectChildFragment");
        } else if (option == IMAGE) {
            choosePhotoFromGallary();
        }
    }

    private void disableUIonLoading() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUIonLoaded() {
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        Fragment selectedFragment = null;
                        String fragmentTag = "";
                        Boolean selected = false;
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                if (bottomNavigation.getMenu().getItem(0).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = HomeFragment.newInstance();
                                    myBundle.putBoolean("newNotifications", newNotifications);
                                    fragmentTag = HOME_TAG;
                                }
                                break;
                            case R.id.action_calendar:
                                if (bottomNavigation.getMenu().getItem(1).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = CalendarFragment.newInstance();
                                }

                                break;
                            case R.id.action_chats:
                                if (bottomNavigation.getMenu().getItem(2).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = ChatsFragment.newInstance();
                                    fragmentTag = CHAT_TAG;
                                }
                                break;
                            case R.id.action_subjects:
                                if (bottomNavigation.getMenu().getItem(3).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = SubjectsFragment.newInstance();
                                }
                                break;
//                            case R.id.action_tasks:
//                                if (bottomNavigation.getMenu().getItem(4).isChecked())
//                                    selected = true;
//                                else {
//                                    selectedFragment = TasksFragment.newInstance();
//                                }
//                                break;

                        }
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        if (!selected) {
                            selectedFragment.setArguments(myBundle);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment, fragmentTag);
                            transaction.commit();
                        }
                        return true;
                    }
                });
    }

    private void getProfileImage() {
        File f = getBaseContext().getFileStreamPath(userId.toString());

        if (f.exists()) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap profile = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

            if (profile != null)
                profilePhoto.setImageBitmap(profile);

        } else
            new getProfileImageTask(token).execute();
    }

//    public void setBadge(Integer num){
//        if (num == 0)
//            badge.reset();
//        else{
//            badge.setBadgeNumber(num);
//        }
//    }
//
//    public void moveBadge(){
//        badge.setGravityOffset(50, 0, true);
//    }
//    public void undoMoveBadge(){
//        badge.setGravityOffset(0, 0, true);
//    }
//    public void addOneBadge(){
//        badge.setBadgeNumber(badge.getBadgeNumber() + 1);
//    }
//    public void removeOneBadge(){
//        badge.setBadgeNumber(badge.getBadgeNumber() - 1);
//    }

    public class initTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject postData;

        public initTask(JSONObject postData) {
            if (postData != null) {
                this.postData = postData;
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            MyFirebaseInstanceIDService f = new MyFirebaseInstanceIDService();
            f.onTokenRefresh();
            firebaseToken = f.getRefreshedToken();
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            connectWebSocket();

            JSONObject response = null;
            try {
                response = NetworkUtils.schoolAuthRefresh(token);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            if (result != null && !result.has("error")) {
                try {
                    SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", result.getString("token"));
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startHome();
            } else {
                launchLogin();
            }
        }
    }

    public class startRequestTask extends AsyncTask<Void, Void, Boolean> {

        JSONObject token;

        public startRequestTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resp = NetworkUtils.schoolUser(token);
                if (resp != null) {
                    //set parent's children
                    if (students.size() == 0) {
                        JSONArray children = (JSONArray) resp.get("children");
                        for (int i = 0; i < children.length(); i++) {
                            JSONObject object = (JSONObject) children.get(i);
                            ArrayList<Subject> subjects = new ArrayList<>();
                            Class studentClass = new Class((JSONObject) object.get("attends"));
                            Student student = new Student(object, subjects, studentClass);
                            students.put(student.getId(), student);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            users = new ArrayList<>();
            students = new HashMap<>();
            disableUIonLoading();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                if (getStudentId() == 0) {
                    try {
                        studentId = (Integer) students.keySet().toArray()[0];
                        classId = students.get(studentId).getStudentClass().getId();
                    } catch (Exception e) {
                        studentId = getStudentId();
                    }
                    saveStudent();
                }
                studentName.setText(students.size() != 0 ? students.get(studentId).toString() : getUserName());

                setNavigation();

                loadHome();

            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    public class getProfileImageTask extends AsyncTask<Void, Void, Void> {

        JSONObject token;
        Bitmap bitmap;

        public getProfileImageTask(JSONObject token) {
            this.token = token;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                bitmap = NetworkUtils.schoolGetProfileImage(token, userId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void param) {
            if (bitmap != null)
                profilePhoto.setImageBitmap(bitmap);
        }
    }

    public class saveProfileImageTask extends AsyncTask<Void, Void, Void> {

        JSONObject token;
        String path;

        public saveProfileImageTask(JSONObject token, String path) {
            this.token = token;
            this.path = path;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                NetworkUtils.schoolUpdateProfileImage(token, path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //TODO save data in db as cache
//    public class saveDataTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            saveData();
//            return null;
//        }
//    }
}
