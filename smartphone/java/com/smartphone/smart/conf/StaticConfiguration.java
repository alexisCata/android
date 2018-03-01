package com.cathedralsw.schoolparent.conf;

/**
 * Created by alexis on 26/10/17.
 */

public class StaticConfiguration {

    public final static String SERVER = Secret.SERVER;

    public final static String SCHOOL_BASE_URL = "http://" + Secret.SERVER + ":8000/api";
    public final static String TOKEN_HEADER = "Authorization";
    public final static String TOKEN_VALUE = "JWT ";
    public final static String SCHOOL_AUTH = "auth/";
    public final static String SCHOOL_AUTH_REFRESH = "auth/refresh/";
    public final static String SCHOOL_USER = "auth/user/";
    public final static String SCHOOL_NOTIFICATIONS = "notifications/";
    public final static String SCHOOL_USERS = "users/";
    public final static String SCHOOL_CHATS = "chats/";
    public final static String SCHOOL_PROFILE_IMAGE = "auth/user/avatar/image.png";
    public final static String SCHOOL_USERS_PROFILE_IMAGE = "users/%s/avatar/image.png";
    public final static String SCHOOL_DASHBOARD = "dashboard/";

    public final static String WEBSOCKET_URL = "ws://" + SERVER + ":8888/ws?token=%s&idandroid=%s&fbtoken=%s";

    public final static String GENERIC = "GENERIC";
    public final static String TASK = "TASK";
    public final static String EXAM = "EXAM";
    public final static String ABSENCE = "ABSENCE";

    public final static String HOME_TAG = "HOME_FRAGMENT";
    public final static String CHAT_TAG = "CHAT_FRAGMENT";
    public final static String USERS_TAG = "USERS_FRAGMENT";

    public static final String IMAGE_DIRECTORY = "/school_images";
    public static final String PROFILE_IMG = "profile.png";

    public static final Integer CAMERA = 11;
    public static final Integer GALLERY = 22;

    public static final Integer STUDENT = 123;
    public static final Integer IMAGE = 321;

    public static final String ORDER_TIMESTAMP =  "timestamp";
    public static final String ORDER_TIMESTAMP_DESC =  "-timestamp";
    public static final String ORDER_DATE =  "date";
    public static final String ORDER_DATE_DESC =  "-date";

}
