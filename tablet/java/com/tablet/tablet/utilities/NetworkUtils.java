package com.cathedralsw.schoolteacher.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.cathedralsw.schoolteacher.classes.MultipartUtility;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexis on 25/09/17.
 */

public class NetworkUtils {
    final static String SCHOOL_BASE_URL = StaticConfiguration.SCHOOL_BASE_URL;
    final static String TOKEN_HEADER = StaticConfiguration.TOKEN_HEADER;
    final static String TOKEN_VALUE = StaticConfiguration.TOKEN_VALUE;
    final static String SCHOOL_AUTH = StaticConfiguration.SCHOOL_AUTH;
    final static String SCHOOL_AUTH_REFRESH = StaticConfiguration.SCHOOL_AUTH_REFRESH;
    final static String SCHOOL_USER = StaticConfiguration.SCHOOL_USER;
    final static String SCHOOL_PARENTS = StaticConfiguration.SCHOOL_PARENTS;
    final static String SCHOOL_NOTIFICATIONS = StaticConfiguration.SCHOOL_NOTIFICATIONS;
    final static String SCHOOL_USERS = StaticConfiguration.SCHOOL_USERS;
    final static String SCHOOL_CHATS = StaticConfiguration.SCHOOL_CHATS;
    final static String SCHOOL_CLASSES = StaticConfiguration.SCHOOL_CLASSES;
    final static String SCHOOL_CLASS_SUBJECTS = StaticConfiguration.SCHOOL_CLASS_SUBJECTS;
    final static String SCHOOL_SCHEDULE = StaticConfiguration.SCHOOL_SCHEDULE;
    final static String SCHOOL_PROFILE_IMAGE = StaticConfiguration.SCHOOL_PROFILE_IMAGE;
    final static String IMAGE_DIRECTORY = StaticConfiguration.IMAGE_DIRECTORY;
    final static String PROFILE_IMG = StaticConfiguration.PROFILE_IMG;
    final static String SCHOOL_USERS_PROFILE_IMAGE = StaticConfiguration.SCHOOL_USERS_PROFILE_IMAGE;
    final static String SCHOOL_DASHBOARD = StaticConfiguration.SCHOOL_DASHBOARD;
    final static String SCHOOL_SCORE = StaticConfiguration.SCHOOL_SCORE;
    final static String SCHOOL_SCORE_ALL = StaticConfiguration.SCHOOL_SCORE_ALL;


    public static JSONObject schoolAuth(JSONObject postData) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_AUTH)
                .build();

        URL auth_url = null;
        try {
            auth_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = (HttpURLConnection) auth_url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        Writer os = new OutputStreamWriter(conn.getOutputStream());
        os.write(postData.toString());

        os.flush();
        os.close();

        JSONObject response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponse(conn);
            try {
                response.put("email", postData.getString("email"));
//                response.put("password", postData.getString("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response = new JSONObject("{\"error\": \"wrong credentials\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        conn.disconnect();

        return response;
    }

    public static JSONObject schoolAuthRefresh(JSONObject postData) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_AUTH_REFRESH)
                .build();

        URL auth_url = null;
        try {
            auth_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = (HttpURLConnection) auth_url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        Writer os = new OutputStreamWriter(conn.getOutputStream());
        os.write(postData.toString());

        os.flush();
        os.close();

        JSONObject response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponse(conn);
        } else {
            try {
                response = new JSONObject("{\"error\": \"wrong credentials\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        conn.disconnect();

        return response;
    }

    public static JSONObject schoolUser(JSONObject token) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_USER)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONObject response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponse(conn);
        }

        conn.disconnect();

        return response;
    }

    public static JSONArray schoolUserParents(JSONObject token, Integer id) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(String.format(SCHOOL_PARENTS, id.toString()))
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }

    public static JSONObject parseResponse(HttpURLConnection conn) {
        String res;
        JSONObject data = null;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            res = response.toString();

            data = new JSONObject(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;

    }

    public static JSONArray parseResponseArray(HttpURLConnection conn) {
        String res;
        JSONArray data = null;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            res = response.toString();

            data = new JSONArray(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;

    }

    public static String parseImage(HttpURLConnection conn) {
        String res = "";
        JSONObject data = null;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            res = response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    public static JSONArray schoolNotifications(JSONObject token, Integer numItems, Integer fromPosition, String fromDate, String toDate, String order, Integer subjectId, Integer classId, String type) throws IOException {

        Uri builtUri;
        Uri.Builder builderUri = Uri.parse(SCHOOL_BASE_URL).buildUpon().appendPath(SCHOOL_NOTIFICATIONS);

        if (numItems != null)
            builderUri.appendQueryParameter("size", numItems.toString());

        if (fromPosition != null)
            builderUri.appendQueryParameter("from", fromPosition.toString());

        if (fromDate != null)
            builderUri.appendQueryParameter("from_date", fromDate);
        if (toDate != null)
            builderUri.appendQueryParameter("to_date", toDate);

        if (order != null)
            builderUri.appendQueryParameter("order_by", order);

        if (subjectId != null)
            builderUri.appendQueryParameter("subject", subjectId.toString());

        if (classId != null)
            builderUri.appendQueryParameter("class", classId.toString());

        if (type != null)
            builderUri.appendQueryParameter("type", type);

        builtUri = builderUri.build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }


    public static JSONArray schoolUsers(JSONObject token) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_USERS)
                .appendQueryParameter("no_students", "")
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }

    public static JSONArray schoolChats(JSONObject token) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_CHATS)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }

    public static JSONArray schoolChatMessage(JSONObject token, Integer userId, String messageId) throws IOException {

        Uri builtUri;
        if (messageId == null || messageId.equals("")) {
            builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                    .appendPath(SCHOOL_CHATS.concat(userId.toString()).concat("/"))
                    .build();
        } else {
            builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                    .appendPath(SCHOOL_CHATS.concat(userId.toString()).concat("/"))
                    .appendQueryParameter("from", messageId)
                    .build();
        }


        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            try {
                response = parseResponseArray(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        conn.disconnect();

        return response;
    }

    public static JSONArray schoolClasses(JSONObject token) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_CLASSES)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }

    public static JSONObject schoolClass(JSONObject token, Integer classId) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_CLASSES.concat(classId.toString()).concat("/"))
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONObject response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponse(conn);
        }

        conn.disconnect();

        return response;
    }

    public static JSONArray schoolSubjectsClass(JSONObject token, Integer subjectId) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_CLASS_SUBJECTS)
                .appendQueryParameter("class", subjectId.toString())
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }


    public static Object[] schoolSendNotification(JSONObject token, JSONObject notification) throws IOException {
        Uri builtUri = null;
        try {

            builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                    .appendPath(SCHOOL_NOTIFICATIONS)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoOutput(true);

        Writer os = new OutputStreamWriter(conn.getOutputStream());
        os.write(notification.toString());

        os.flush();
        os.close();

        Integer responseCode = conn.getResponseCode();

        JSONObject notifJSON = parseResponse(conn);
        SchoolNotification notif = new SchoolNotification(notifJSON);

        conn.disconnect();

        Object[] response = new Object[2];
        response[0] = responseCode;
        response[1] = notif;

        return response;
    }

    public static JSONArray schoolSchedule(JSONObject token) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_SCHEDULE)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }

    public static Boolean schoolDeleteNotification(JSONObject token, Integer id) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_NOTIFICATIONS.concat(id.toString()).concat("/"))
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        Boolean response = false;
        if (conn.getResponseCode() == 204) {
            response = true;
        }

        conn.disconnect();

        return response;
    }

    public static Object[] schoolUpdateNotification(JSONObject token, Integer id, JSONObject notification) throws IOException {
        Uri builtUri = null;
        try {

            builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                    .appendPath(SCHOOL_NOTIFICATIONS.concat(id.toString() + "/"))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoOutput(true);

        Writer os = new OutputStreamWriter(conn.getOutputStream());
        os.write(notification.toString());

        os.flush();
        os.close();

        Integer responseCode = conn.getResponseCode();

        JSONObject notifJSON = parseResponse(conn);
        SchoolNotification notif = new SchoolNotification(notifJSON);

        conn.disconnect();

        Object[] response = new Object[2];
        response[0] = responseCode;
        response[1] = notif;

        return response;
    }


    public static Integer schoolUpdateProfileImage(JSONObject token, String path) throws IOException {
        Uri builtUri = null;
        try {

            builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                    .appendPath(SCHOOL_PROFILE_IMAGE)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String charset = "UTF-8";
        File uploadFile1 = new File(path);
        String requestURL = builtUri.toString();

        MultipartUtility multipart = new MultipartUtility(requestURL, charset, token_value);
        multipart.addFormField("friend_id", "Cool Pictures");
        multipart.addFormField("userid", "Java,upload,Spring");

        multipart.addFilePart("file", uploadFile1);

        multipart.finish();

        return 0;
    }


    public static Bitmap schoolGetProfileImage(JSONObject token, Integer userId, Context context) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_PROFILE_IMAGE)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);

        Bitmap image = null;

        if (conn.getResponseCode() == 200) {

            InputStream in = conn.getInputStream();
            image = BitmapFactory.decodeStream(in);

//            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
//
//            if (!wallpaperDirectory.exists()) {
//                wallpaperDirectory.mkdirs();
//            }
            File f = new File(context.getFilesDir(), userId.toString());
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileOutputStream out = new FileOutputStream(f);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

        }

        conn.disconnect();

        return image;
    }

    public static Bitmap schoolGetUsersProfileImage(JSONObject token, Integer userId, Context context) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(String.format(SCHOOL_USERS_PROFILE_IMAGE, userId.toString()))
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);

        Bitmap image = null;

        if (conn.getResponseCode() == 200) {

            InputStream in = conn.getInputStream();
            image = BitmapFactory.decodeStream(in);

            if (image != null) {
                File f = new File(context.getFilesDir(), userId.toString());
                try {
                    f.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FileOutputStream out = new FileOutputStream(f);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            }
        }

        conn.disconnect();

        return image;
    }

    public static JSONObject schoolGetUserDashboard(JSONObject token, Integer userId) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_DASHBOARD.concat(userId.toString()).concat("/"))
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONObject response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponse(conn);
        }

        return response;
    }

    public static JSONArray schoolNotificationsStudentType(JSONObject token, Integer studentId, String type) throws IOException {

        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(SCHOOL_NOTIFICATIONS)
                .appendQueryParameter("student", studentId.toString())
                .appendQueryParameter("type", type)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);
        conn.setDoInput(true);


        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        conn.disconnect();

        return response;
    }


    public static JSONArray schoolScoreAll(JSONObject token, Integer notificationId) throws IOException {
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(String.format(SCHOOL_SCORE_ALL, notificationId.toString()))
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);

        JSONArray response = null;
        if (conn.getResponseCode() == 200) {
            response = parseResponseArray(conn);
        }

        return response;
    }


    public static JSONObject schoolSetScore(JSONObject token, Integer notificationId, Integer userId, JSONObject data) throws IOException {
        String url = String.format(SCHOOL_SCORE, notificationId.toString()).concat(userId.toString()).concat("/");
        Uri builtUri = Uri.parse(SCHOOL_BASE_URL).buildUpon()
                .appendPath(url)
                .build();

        URL notifications_url = null;
        try {
            notifications_url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String token_value = null;
        try {
            token_value = TOKEN_VALUE.concat(token.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpURLConnection conn = (HttpURLConnection) notifications_url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(TOKEN_HEADER, token_value);

        Writer os = new OutputStreamWriter(conn.getOutputStream());
        os.write(data.toString());

        os.flush();
        os.close();

        JSONObject response = null;
        if (conn.getResponseCode() == 201) {
            response = parseResponse(conn);
        }

        return response;
    }

}