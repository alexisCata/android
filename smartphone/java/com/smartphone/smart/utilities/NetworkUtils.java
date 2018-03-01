package com.cathedralsw.schoolparent.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.cathedralsw.schoolparent.classes.MultipartUtility;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    final static String SCHOOL_NOTIFICATIONS = StaticConfiguration.SCHOOL_NOTIFICATIONS;
    final static String SCHOOL_USERS = StaticConfiguration.SCHOOL_USERS;
    final static String SCHOOL_CHATS = StaticConfiguration.SCHOOL_CHATS;
    final static String SCHOOL_PROFILE_IMAGE = StaticConfiguration.SCHOOL_PROFILE_IMAGE;
    final static String IMAGE_DIRECTORY = StaticConfiguration.IMAGE_DIRECTORY;
    final static String PROFILE_IMG = StaticConfiguration.PROFILE_IMG;
    final static String SCHOOL_USERS_PROFILE_IMAGE = StaticConfiguration.SCHOOL_USERS_PROFILE_IMAGE;
    public static String SCHOOL_DASHBOARD = StaticConfiguration.SCHOOL_DASHBOARD;

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

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(postData.toString());

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

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(postData.toString());

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

    public static JSONArray schoolNotifications(JSONObject token, Integer studentId, Integer numItems, Integer fromPosition, String fromDate, String toDate, String order, Integer subjectId, String type) throws IOException {

        Uri builtUri;
        Uri.Builder builderUri = Uri.parse(SCHOOL_BASE_URL).buildUpon().appendPath(SCHOOL_NOTIFICATIONS);
        builderUri.appendQueryParameter("student", studentId.toString());

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
            image.compress(Bitmap.CompressFormat.PNG, 70, out);
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

}