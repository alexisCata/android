package com.cathedralsw.schoolparent.screens;

import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;
import com.cathedralsw.schoolparent.utilities.DBHelper;
import com.cathedralsw.schoolparent.utilities.DateDecorator;
import com.cathedralsw.schoolparent.utilities.NetworkUtils;
import com.cathedralsw.schoolparent.utilities.NotificationsAdapter;
import com.cathedralsw.schoolparent.utilities.Utils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by alexis on 27/09/17.
 */

public class CalendarFragment extends Fragment implements NotificationsAdapter.ListItemClickListener {

    private ArrayList<String> types;

    private JSONObject token;
    private Integer studentId = 0;

    private ArrayList<SchoolNotification> notifications = new ArrayList();
    private ArrayList<SchoolNotification> notifications_generic = new ArrayList();
    private ArrayList<SchoolNotification> notifications_task = new ArrayList();
    private ArrayList<SchoolNotification> notifications_exam = new ArrayList();
    private ArrayList<SchoolNotification> notifications_absence = new ArrayList();
    private ArrayList<SchoolNotification> notifications_shown = new ArrayList();

    private List<CalendarDay> generic_events_list = new ArrayList();
    private List<CalendarDay> task_events_list = new ArrayList();
    private List<CalendarDay> exam_events_list = new ArrayList();
    private List<CalendarDay> absence_events_list = new ArrayList();

    private Spinner sp_types;
    private NotificationsAdapter mAdapter;
    private RecyclerView rvNotifications;
    private MaterialCalendarView calendar_mv;

    private ProgressBar pbLoading;

    private CalendarDay selectedDate;

    private Integer index = 0;
    private Integer indexOrigin = 0;
    private Integer indexGeneric = 0;
    private Integer indexTask = 0;
    private Integer indexExam = 0;
    private Integer indexAbsence = 0;

    private Boolean loadedCache = false;
    private DBHelper schoolDB;


    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
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
//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_calendar);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            getActivity().setContentView(R.layout.fragment_home_landscape);
//            layout.setOrientation(LinearLayout.HORIZONTAL);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            layout.setOrientation(LinearLayout.VERTICAL);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        getUserData();

        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        rvNotifications = (RecyclerView) view.findViewById(R.id.rv_notifications);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setHasFixedSize(true);
        calendar_mv = (MaterialCalendarView) view.findViewById(R.id.calendar_view);
        sp_types = (Spinner) view.findViewById(R.id.sp_event_type);
        types = new ArrayList<>(Arrays.asList(getActivity().getResources().getString(R.string.all_events),
                getActivity().getResources().getString(R.string.generic), getActivity().getResources().getString(R.string.task),
                getActivity().getResources().getString(R.string.exam), getActivity().getResources().getString(R.string.abscence)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_types.setAdapter(adapter);

        handleCacheData();

        requestNotifications(null);

        return view;
    }

    private void requestNotifications(Integer month) {
        new monthNotificationsTask(token, studentId, month).execute();
    }


    private void saveCacheData() {
        schoolDB.deleteNotificationsCalendar();
        for (SchoolNotification n : notifications_shown) {
            schoolDB.saveNotificationCalendar(n);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        notifications = schoolDB.getNotificationsCalendar();
    }

    private void handleCacheData() {
        getCacheData();
        classifyNotifications();
        mAdapter = new NotificationsAdapter(notifications_shown, getResources(), CalendarFragment.this);
        rvNotifications.setAdapter(mAdapter);
        rvNotifications.scrollToPosition(index);
    }

    private void setListeners() {

        sp_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                calendar_mv.removeDecorators();

                List<CalendarDay> today = new ArrayList<>();
                Calendar today_cal = Calendar.getInstance();
                today_cal.set(Calendar.HOUR_OF_DAY, 0);
                CalendarDay calendarDay = CalendarDay.from(today_cal);
                today.add(calendarDay);

                calendar_mv.addDecorator(new DateDecorator(getActivity(), today, true));
                switch (position) {
                    case 0:
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.primary_darker), generic_events_list));
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.green), task_events_list));
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.red), exam_events_list));
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.black), absence_events_list));
                        mAdapter = new NotificationsAdapter(notifications_shown, getResources(), CalendarFragment.this);
                        rvNotifications.setAdapter(mAdapter);
                        rvNotifications.scrollToPosition(index);
                        break;
                    case 1:
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.primary_darker), generic_events_list));
                        mAdapter = new NotificationsAdapter(notifications_generic, getResources(), CalendarFragment.this);
                        rvNotifications.setAdapter(mAdapter);
                        rvNotifications.scrollToPosition(indexGeneric);
                        break;
                    case 2:
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.green), task_events_list));
                        mAdapter = new NotificationsAdapter(notifications_task, getResources(), CalendarFragment.this);
                        rvNotifications.setAdapter(mAdapter);
                        rvNotifications.scrollToPosition(indexTask);
                        break;
                    case 3:
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.red), exam_events_list));
                        mAdapter = new NotificationsAdapter(notifications_exam, getResources(), CalendarFragment.this);
                        rvNotifications.setAdapter(mAdapter);
                        rvNotifications.scrollToPosition(indexExam);
                        break;
                    case 4:
                        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.black), absence_events_list));
                        mAdapter = new NotificationsAdapter(notifications_absence, getResources(), CalendarFragment.this);
                        rvNotifications.setAdapter(mAdapter);
                        rvNotifications.scrollToPosition(indexAbsence);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        calendar_mv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selectedDate == date) {
                    calendar_mv.clearSelection();
                    selectedDate = null;
                } else {
                    selectedDate = date;
                }
                classifyNotifications();
                mAdapter.notifyDataSetChanged();
                if (selectedDate == null) {
                    rvNotifications.setAdapter(mAdapter);
                    rvNotifications.scrollToPosition(index);
                }

            }
        });

        calendar_mv.setOnMonthChangedListener(new OnMonthChangedListener() {

            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                requestNotifications(date.getMonth());

                selectedDate = null;
//                NotificationsSelectedMonth();
                classifyNotifications();
                mAdapter = new NotificationsAdapter(notifications_shown, getResources(), CalendarFragment.this);
                sp_types.setSelection(0);
                mAdapter.notifyDataSetChanged();
                rvNotifications.setAdapter(mAdapter);

                if (date.getMonth() == (Integer) Calendar.getInstance().get(Calendar.MONTH)) {
                    index = indexOrigin;
                } else {
                    index = 0;
                }
                rvNotifications.scrollToPosition(index);
            }
        });
    }

    @Override
    public void onListItemClick(SchoolNotification notification) {

        NotificationDetailFragment detailFragment = NotificationDetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("notification", notification);
        detailFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, detailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void classifyNotifications() {

        initializeCalendarsDays();
        initializeNotificationCollections();

        Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        CalendarDay calendarDay;

        SchoolNotification notif;
        Date notif_date;

        Calendar auxcal = Calendar.getInstance();
        auxcal.add(Calendar.DATE, +1);
        Date tomorrow = cal.getTime();

        Boolean setIndex = false;
        Boolean setIndexG = false;
        Boolean setIndexT = false;
        Boolean setIndexE = false;
        Boolean setIndexA = false;

        for (int i = 0; i < notifications.size(); i++) {
            notif = notifications.get(i);

            notif_date = notif.getDate();

            if (notif_date.before(tomorrow) && !setIndex) {
                index = i;
                setIndex = true;
            }

            cal.setTime(notif_date);
            calendar.set(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), //months starts from 0
                    cal.get(Calendar.DAY_OF_MONTH));
            calendarDay = CalendarDay.from(calendar);

            if (selectedDate == null || (selectedDate.getYear() == calendarDay.getYear() &&
                    selectedDate.getMonth() == calendarDay.getMonth() &&
                    selectedDate.getDay() == calendarDay.getDay())) {
                notifications_shown.add(notif);
            }

            switch (notif.getType()) {
                case StaticConfiguration.GENERIC:
                    if (notif_date.before(tomorrow) && !setIndexG) {
                        indexGeneric = notifications_generic.size();
                        setIndexG = true;
                    }
                    generic_events_list.add(calendarDay);
                    if (selectedDate == null || (selectedDate.getYear() == calendarDay.getYear() &&
                            selectedDate.getMonth() == calendarDay.getMonth() &&
                            selectedDate.getDay() == calendarDay.getDay())) {
                        notifications_generic.add(notif);
                    }
                    break;
                case StaticConfiguration.TASK:
                    if (notif_date.before(tomorrow) && !setIndexT) {
                        indexTask = notifications_task.size();
                        setIndexT = true;
                    }
                    task_events_list.add(calendarDay);
                    if (selectedDate == null || (selectedDate.getYear() == calendarDay.getYear() &&
                            selectedDate.getMonth() == calendarDay.getMonth() &&
                            selectedDate.getDay() == calendarDay.getDay())) {
                        notifications_task.add(notif);
                    }
                    break;
                case StaticConfiguration.EXAM:
                    if (notif_date.before(tomorrow) && !setIndexE) {
                        indexExam = notifications_exam.size();
                        setIndexE = true;
                    }
                    exam_events_list.add(calendarDay);
                    if (selectedDate == null || (selectedDate.getYear() == calendarDay.getYear() &&
                            selectedDate.getMonth() == calendarDay.getMonth() &&
                            selectedDate.getDay() == calendarDay.getDay())) {
                        notifications_exam.add(notif);
                    }
                    break;
                case StaticConfiguration.ABSENCE:
                    if (notif_date.before(tomorrow) && !setIndexA) {
                        indexAbsence = notifications_absence.size();
                        setIndexA = true;
                    }
                    absence_events_list.add(calendarDay);
                    if (selectedDate == null || (selectedDate.getYear() == calendarDay.getYear() &&
                            selectedDate.getMonth() == calendarDay.getMonth() &&
                            selectedDate.getDay() == calendarDay.getDay())) {
                        notifications_absence.add(notif);
                    }
                    break;
            }
        }
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.primary_darker), generic_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.green), task_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.red), exam_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.black), absence_events_list));
    }

    private void initializeCalendarsDays() {
        generic_events_list.clear();
        task_events_list.clear();
        exam_events_list.clear();
        absence_events_list.clear();
    }

    private void initializeNotificationCollections() {
        notifications_shown.clear();
        notifications_generic.clear();
        notifications_task.clear();
        notifications_exam.clear();
        notifications_absence.clear();
    }

    private void getUserData() {

        token = ((MainActivity) getActivity()).getToken();
        studentId = ((MainActivity) getActivity()).getStudentId();

    }

    public class monthNotificationsTask extends AsyncTask<Boolean, Void, Boolean> {

        JSONObject token;
        Integer studentId;
        Integer month;

        public monthNotificationsTask(JSONObject token, Integer studentId, Integer month) {
            this.token = token;
            this.studentId = studentId;
            this.month = month;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
                Date[] range = Utils.getDateRange(month);

                String begin = dateFormat.format(range[0]);
                String end = dateFormat.format(range[1]);

                JSONArray response = NetworkUtils.schoolNotifications(this.token, this.studentId, null, null, begin, end, StaticConfiguration.ORDER_DATE_DESC, null, null);
                notifications = Utils.parseNotificationsResponse(response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            classifyNotifications();

            if (!loadedCache) {
                saveCacheData();
                loadedCache = true;
            }

            mAdapter = new NotificationsAdapter(notifications_shown, getResources(), CalendarFragment.this);
            rvNotifications.setAdapter(mAdapter);
            rvNotifications.scrollToPosition(index);

            indexOrigin = index;

            List<CalendarDay> today = new ArrayList<>();
            Calendar today_cal = Calendar.getInstance();
            today_cal.set(Calendar.HOUR_OF_DAY, 0);
            CalendarDay calendarDay = CalendarDay.from(today_cal);
            today.add(calendarDay);

//        calendar_mv.addDecorator(new DateDecorator(getActivity(), today, true));
            calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.primary_darker), generic_events_list));
            calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.green), task_events_list));
            calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.red), exam_events_list));
            calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.black), absence_events_list));

            setListeners();

            pbLoading.setVisibility(View.INVISIBLE);

        }
    }
}
