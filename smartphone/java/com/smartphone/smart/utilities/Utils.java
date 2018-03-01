package com.cathedralsw.schoolparent.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
/**
 * Created by alexis on 17/10/17.
 */

public class Utils {

    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static JSONObject getJsonToken(SharedPreferences prefs) {

        JSONObject token = new JSONObject();
        try {
            token.put("token", prefs.getString("token", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static HashMap<String, String> eventsType(Resources resources) {
        HashMap<String, String> types = new HashMap<>();
        types.put("GENERIC", resources.getString(R.string.generic));
        types.put("TASK", resources.getString(R.string.task));
        types.put("EXAM", resources.getString(R.string.exam));
        types.put("ABSENCE", resources.getString(R.string.abscence));


        return types;

    }

    public static String formatDateDayMonth(Date date){
        DateFormat format = new SimpleDateFormat("EEEE dd MMMM", Locale.getDefault());
        String stringDate = format.format(date);
        return toCamelCase(stringDate);
    }

    public static String formatDateYearMonthDay(Date date){
        String shown_date = "";
        try {
            SimpleDateFormat format_output = new SimpleDateFormat("EEEE d MMMM y");
            shown_date = format_output.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toCamelCase(shown_date);
    }


    public static ArrayList<SchoolNotification> parseNotificationsResponse(JSONArray reponse) {
        ArrayList<SchoolNotification> notifications = new ArrayList();

        try {
            for (int i = 0; i < reponse.length(); i++) {
                JSONObject notif = (JSONObject) reponse.get(i);
                SchoolNotification new_notification = new SchoolNotification(notif);
                notifications.add(i, new_notification);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notifications;
    }


    public static Date[] getDateRange(Integer month) {
        Date begining, end;

        {
            Calendar calendar = getCalendar(month);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            if (month != null){
                calendar.set(Calendar.MONTH, month);
            }

            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
        }

        {
            Calendar calendar = getCalendar(month);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
        }

        Date[] range = {begining, end};

        return range;
    }

    private static Calendar getCalendar(Integer month) {
        Calendar calendar = GregorianCalendar.getInstance();
        if (month!= null) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, month);
        }
        else
            calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }


}
