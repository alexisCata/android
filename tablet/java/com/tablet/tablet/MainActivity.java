package com.cathedralsw.schoolteacher;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.cathedralsw.schoolteacher.classes.DBHelper;
import com.cathedralsw.schoolteacher.classes.ISelectedParent;
import com.cathedralsw.schoolteacher.classes.MyFirebaseInstanceIDService;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.User;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;
import com.cathedralsw.schoolteacher.screens.ChatFragment;
import com.cathedralsw.schoolteacher.screens.ChatsFragment;
import com.cathedralsw.schoolteacher.screens.HomeFragment;
import com.cathedralsw.schoolteacher.screens.NewNotificationFragment;
import com.cathedralsw.schoolteacher.screens.SubjectsFragment;
import com.cathedralsw.schoolteacher.screens.TasksFragment;
import com.cathedralsw.schoolteacher.utilities.BottomNavigationViewHelper;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.NotificationsComparator;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cathedralsw.schoolteacher.utilities.Utils.getJsonToken;

public class MainActivity extends AppCompatActivity implements ISelectedParent {

    private final String WEBSOCKET_URL = StaticConfiguration.WEBSOCKET_URL;
    private final String HOME_TAG = StaticConfiguration.HOME_TAG;
    private final String CHAT_TAG = StaticConfiguration.CHAT_TAG;
    private final Integer CAMERA = 11;
    private final Integer GALLERY = 22;
    private final String IMAGE_DIRECTORY = StaticConfiguration.IMAGE_DIRECTORY;
    private final String PROFILE_IMG = StaticConfiguration.PROFILE_IMG;
    public SchoolNotification deletedNotification;
    public SchoolNotification editedNotification;
    public SchoolNotification addedNotification;
    private Integer userId;
    private ArrayList<SchoolNotification> notifications = new ArrayList();
    private ArrayList<User> users = new ArrayList();
    //    private ArrayList<Class> classes = new ArrayList();
//    private ArrayList<Chat> chats = new ArrayList();
//    private ArrayList<Schedule> schedule = new ArrayList();
//    private ArrayList<Schedule> scheduleWeek = new ArrayList();
//    private ArrayList<TeacherSubject> subjects = new ArrayList<>();
    private Bundle myBundle = new Bundle();
    private Toolbar mainToolBar;
    private CircleImageView profilePhoto;
    private BottomNavigationView bottomNavigation;
    private TextView userName;
    private ProgressBar progressBar;
    private JSONObject token;
    private WebSocketClient mWebSocketClient;
    private String androidId;
    private String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // FIXING ORIENTATION
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (!loginSaved()) {
            launchLogin();
        } else {
//            getStoredData();
            new initTask(getJsonToken(getSharedPreferences("creds", MODE_PRIVATE))).execute();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

        mainToolBar = (Toolbar) findViewById(R.id.toolbar);
        profilePhoto = (CircleImageView) findViewById(R.id.student_photo);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);
//            progressBar.getIndeterminateDrawable()
//                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        userName = (TextView) findViewById(R.id.tv_student_name);

        userName.setText(getUserName());
        userId = getUserId();

        setSupportActionBar(mainToolBar);

        getProfileImage();

        setListeners();

        setNavigation();

        loadHome();

//        View a = (View) bottomNavigation.findViewById(R.id.action_chats);
//        QBadgeView bd = new QBadgeView(getApplicationContext());
//        bd.bindTarget(a);
//        bd.setBadgeNumber(5);
//
////        bd.setBadgePadding(10, true);
//        bd.setGravityOffset(40, 5, true);

    }

    private void setListeners() {
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallary();
//                showPictureDialog();
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
            File f = new File(getApplicationContext().getFilesDir(), userId.toString());
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private String getUserName() {
        SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
        String userName = sharedPref.getString("first_name", "").concat(" ").concat(sharedPref.getString("last_name", ""));
        return userName;
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

    private Integer getUserId() {
        SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
        userId = sharedPref.getInt("userId", 0);
        return userId;
    }

    public JSONObject getToken() {
        return token;
    }

    @Override
    public void onSelectedParent(User parent) {
        //SubjectDetailFragment
        chatWithParent(parent);
    }

    private void chatWithParent(User parent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userTo", parent);

        ChatFragment chat = ChatFragment.newInstance();
        chat.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, chat, "");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public WebSocketClient getWebSocketClient() {
        return mWebSocketClient;
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

    private void sendNotification(JSONObject notification) {
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
//            loadNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(JSONObject notification, String userFrom) {
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

    private ChatFragment getChatFragment() {
        ChatFragment activeChat = (ChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_TAG);
        return activeChat;
    }

    public void addNotification(SchoolNotification notification, Boolean add) {
        if (add)
            notifications.add(notification);
        else {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getId() == notification.getId())
                    notifications.remove(notifications.get(i));
                break;
            }
            notifications.add(notification);
        }

        Collections.sort(notifications, new NotificationsComparator());
        setNavigation();
    }

    public void deleteNotification(SchoolNotification notification) {
        notifications.remove(notification);
        setNavigation();
    }

    public void loadChats() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            new notificationsLoadTask(token).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        else
//            new notificationsLoadTask(token).execute(false);
        HomeFragment activeHomeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
        Boolean homeActive = activeHomeFragment != null && activeHomeFragment.isVisible() ? true : false;
        if (homeActive) {
            activeHomeFragment.requestChats();
        }

        ChatsFragment activeChatsFragment = (ChatsFragment) getSupportFragmentManager().findFragmentByTag(CHAT_TAG);
        Boolean chatsActive = activeChatsFragment != null && activeChatsFragment.isVisible() ? true : false;
        if (chatsActive) {
            activeChatsFragment.requestNewChats();
        }
    }

    public void disableUIonLoading() {
//        progressBar.setVisibility(View.VISIBLE);
        profilePhoto.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUIonLoaded() {
//        progressBar.setVisibility(View.INVISIBLE);
        profilePhoto.setEnabled(true);
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
                                    fragmentTag = HOME_TAG;
                                }
                                break;
                            case R.id.action_subjects:
                                if (bottomNavigation.getMenu().getItem(1).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = SubjectsFragment.newInstance();
                                }
                                break;
                            case R.id.action_tasks:
                                if (bottomNavigation.getMenu().getItem(2).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = TasksFragment.newInstance();
                                }
                                break;
                            case R.id.action_chats:
                                if (bottomNavigation.getMenu().getItem(3).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = ChatsFragment.newInstance();
                                }
                                fragmentTag = CHAT_TAG;
                                break;
                            case R.id.action_new_notification:
                                if (bottomNavigation.getMenu().getItem(4).isChecked())
                                    selected = true;
                                else {
                                    selectedFragment = NewNotificationFragment.newInstance();
                                }

                                break;

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

    public class getProfileImageTask extends AsyncTask<Void, Void, Bitmap> {

        JSONObject token;

        public getProfileImageTask(JSONObject token) {
            this.token = token;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                bitmap = NetworkUtils.schoolGetProfileImage(token, userId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (image != null)
                profilePhoto.setImageBitmap(image);
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
}
