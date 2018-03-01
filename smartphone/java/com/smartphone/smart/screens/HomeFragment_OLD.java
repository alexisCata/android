package com.cathedralsw.schoolparent.screens;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;
import com.cathedralsw.schoolparent.utilities.DateDecorator;
import com.cathedralsw.schoolparent.utilities.NotificationsAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by alexis on 27/09/17.
 */

public class HomeFragment_OLD extends Fragment {

    private ArrayList<SchoolNotification> notifications = new ArrayList();
    private ArrayList<SchoolNotification> notificationsShown = new ArrayList();
    private NotificationsAdapter mAdapter;
    private RecyclerView rvNotifications;
    private MaterialCalendarView calendar_mv;
    private Integer index;
    private Integer indexOrigin;

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static HomeFragment_OLD newInstance() {
        HomeFragment_OLD fragment = new HomeFragment_OLD();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_home);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            layout.setOrientation(LinearLayout.HORIZONTAL);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            layout.setOrientation(LinearLayout.VERTICAL);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_old, container, false);

//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_home);
//        switch (getResources().getConfiguration().orientation){
//            case Configuration.ORIENTATION_PORTRAIT:
//                layout.setOrientation(LinearLayout.VERTICAL);
//
//                break;
//            case Configuration.ORIENTATION_LANDSCAPE:
//                layout.setOrientation(LinearLayout.HORIZONTAL);
//                break;
//        }

        view = initialize(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        rvNotifications.scrollToPosition(index);

    }

    private View initialize(View view) {

        notifications = getArguments().getParcelableArrayList("notifications");

        rvNotifications = (RecyclerView) view.findViewById(R.id.rv_notifications);
        calendar_mv = (MaterialCalendarView) view.findViewById(R.id.calendar_view);

        classifyNotifications();

        setListeners();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setHasFixedSize(true);
        mAdapter = new NotificationsAdapter(notificationsShown, getResources());
        rvNotifications.setAdapter(mAdapter);
        rvNotifications.scrollToPosition(index);

        return view;
    }

    private void classifyNotifications() {
        Calendar calendar = Calendar.getInstance();
        CalendarDay calendarDay;
        List<CalendarDay> generic_events_list = new ArrayList<>();
        List<CalendarDay> task_events_list = new ArrayList<>();
        List<CalendarDay> exam_events_list = new ArrayList<>();
        List<CalendarDay> absence_events_list = new ArrayList<>();

        SchoolNotification notif;
        Date notif_date;
        Calendar cal = Calendar.getInstance();
        Calendar calToday = Calendar.getInstance();
        Calendar calIndex = Calendar.getInstance();
        Date dayToday = new Date();
        index = 0;

        notificationsShown = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            notif = notifications.get(i);

            notif_date = notif.getDate();
            cal.setTime(notif_date);
            calToday.setTime(dayToday);

            if (((Integer) cal.get(Calendar.MONTH)) == calendar_mv.getCurrentDate().getMonth()) {
                if ((cal.get(Calendar.YEAR) <= calToday.get(Calendar.YEAR) &&
                        cal.get(Calendar.MONTH) <= calToday.get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH) <= calToday.get(Calendar.DAY_OF_MONTH)) &&
                        (cal.get(Calendar.YEAR) == calIndex.get(Calendar.YEAR) &&
                                cal.get(Calendar.MONTH) == calIndex.get(Calendar.MONTH) &&
                                cal.get(Calendar.DAY_OF_MONTH) != calIndex.get(Calendar.DAY_OF_MONTH))) {
                    index = notificationsShown.size();
                    calIndex = (Calendar) cal.clone();
                }
                notificationsShown.add(notif);
            }

            calendar.set(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), //months starts from 0
                    cal.get(Calendar.DAY_OF_MONTH));
            calendarDay = CalendarDay.from(calendar);

            switch (notif.getType()) {
                case StaticConfiguration.GENERIC:
                    generic_events_list.add(calendarDay);
                    break;
                case StaticConfiguration.TASK:
                    task_events_list.add(calendarDay);
                    break;
                case StaticConfiguration.EXAM:
                    exam_events_list.add(calendarDay);
                    break;
                case StaticConfiguration.ABSENCE:
                    absence_events_list.add(calendarDay);
                    break;
            }
        }
        indexOrigin = index;

        List<CalendarDay> today = new ArrayList<>();
        Calendar today_cal = Calendar.getInstance();
        today_cal.set(Calendar.HOUR_OF_DAY, 0);
        calendarDay = CalendarDay.from(today_cal);
        today.add(calendarDay);

        calendar_mv.addDecorator(new DateDecorator(getActivity(), today, true));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.primary_darker), generic_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.green), task_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.red), exam_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.black), absence_events_list));
    }

    private void setListeners() {
        calendar_mv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                ArrayList<SchoolNotification> events = new ArrayList<>();

                for (int i = 0; i < notificationsShown.size(); i++) {
                    SchoolNotification notif = notificationsShown.get(i);
                    if (notif.getDate().equals(date.getDate())) {
                        events.add(notif);
                    }
                }
                if (events.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data", events);

                    NotificationDetailListFragment dayDetailFragment = new NotificationDetailListFragment();
                    dayDetailFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, dayDetailFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        calendar_mv.setOnMonthChangedListener(new OnMonthChangedListener() {

            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                notificationsSelectedMonth();
            }
        });
    }

    private void notificationsSelectedMonth() {
        SchoolNotification notif;
        Date notif_date;
        Calendar cal = Calendar.getInstance();
        notificationsShown = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            notif = notifications.get(i);

            notif_date = notif.getDate();
            cal.setTime(notif_date);

            if (((Integer) cal.get(Calendar.MONTH)) == calendar_mv.getCurrentDate().getMonth()) {
                notificationsShown.add(notif);
            }
        }

        if ((Integer) Calendar.getInstance().get(Calendar.MONTH) == calendar_mv.getCurrentDate().getMonth()) {
            index = indexOrigin;
        } else {
            index = 0;
        }
        mAdapter = new NotificationsAdapter(notificationsShown, getResources());
        rvNotifications.setAdapter(mAdapter);
        rvNotifications.scrollToPosition(index);
    }

}
