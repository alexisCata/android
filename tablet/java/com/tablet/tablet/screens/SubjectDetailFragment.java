package com.cathedralsw.schoolteacher.screens;

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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.DBHelper;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.Student;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;
import com.cathedralsw.schoolteacher.classes.User;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;
import com.cathedralsw.schoolteacher.utilities.DateDecorator;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.NotificationsAdapterSubject;
import com.cathedralsw.schoolteacher.utilities.NotificationsComparatorReverse;
import com.cathedralsw.schoolteacher.utilities.StudentsAdapter;
import com.cathedralsw.schoolteacher.utilities.StudentsComparator;
import com.cathedralsw.schoolteacher.utilities.Utils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by alexis on 9/10/17.
 */

public class SubjectDetailFragment extends Fragment implements NotificationsAdapterSubject.ListItemClickListener, StudentsAdapter.ListItemClickListener {

    private ArrayList<SchoolNotification> subjectTasks = new ArrayList<>();
    private ArrayList<SchoolNotification> subjectExams = new ArrayList<>();
    private ArrayList<SchoolNotification> subjectAll = new ArrayList<>();
    private ArrayList<SchoolNotification> subjectTasksShown = new ArrayList<>();
    private ArrayList<SchoolNotification> subjectExamsShown = new ArrayList<>();
    private ArrayList<SchoolNotification> subjectAllShown = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private TeacherSubject subject = null;

    private ProgressBar pbLoading;
    private MaterialCalendarView calendar_mv;
    private NotificationsAdapterSubject tasksAdapter, examsAdapter, allAdapter;
    private StudentsAdapter studentsAdapter;
    private RecyclerView tasksList, examsList, allList, studentsList;
    private ImageButton newTask, newExam;
    private TextView subjectTitle, subjectClass;
    private Integer index = 0;
    private Integer indexTasks = 0;
    private Integer indexExams = 0;
    private Integer indexOrigin = 0;
    private CalendarDay selectedDate;
    private JSONObject token;
    private DBHelper schoolDB;
    private Boolean loadedCacheNotifications = false;
    private Boolean loadedCacheStudents = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_subject_detail, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        token = ((MainActivity) getActivity()).getToken();

        subject = getArguments().getParcelable("subject");

        checkIfEditOrDelete();

        getCacheData();

        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        calendar_mv = (MaterialCalendarView) view.findViewById(R.id.calendar_view);
        subjectTitle = (TextView) view.findViewById(R.id.tv_subject_name);
        subjectClass = (TextView) view.findViewById(R.id.tv_class);
        tasksList = (RecyclerView) view.findViewById(R.id.rv_tasks_list);
        examsList = (RecyclerView) view.findViewById(R.id.rv_exams_list);
        allList = (RecyclerView) view.findViewById(R.id.rv_notifications);
        studentsList = (RecyclerView) view.findViewById(R.id.rv_students);
        newTask = (ImageButton) view.findViewById(R.id.btn_new_task);
        newExam = (ImageButton) view.findViewById(R.id.btn_new_exam);

        subjectTitle.setText(subject.getName());
        subjectClass.setText(subject.getSubjectClass().getName());

        clasifyNotifications();
        setStudents();

        if (subjectAll.isEmpty() || !loadedCacheNotifications)
            requestNotifications(null);

        if (students.isEmpty() || !loadedCacheStudents)
            requestStudents();

        setListeners();

        return view;
    }

    private void saveCacheNotifications() {
        schoolDB.deleteNotificationsSubject(subject.getSubjectClass().getId(), subject.getId());
        for (SchoolNotification n : subjectAll) {
            schoolDB.saveNotificationSubject(n);
        }
    }

    private void saveCacheStudents() {
        schoolDB.deleteStudentsClass(subject.getSubjectClass().getId());
        for (Student s : students) {
            schoolDB.saveStudentClass(s, subject.getSubjectClass().getId());
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        subjectAll = schoolDB.getNotificationsSubject(subject.getSubjectClass().getId(), subject.getId());
        Collections.sort(subjectAll, new NotificationsComparatorReverse());
        students = schoolDB.getStudentsClass(subject.getSubjectClass().getId());
    }

    private void checkIfEditOrDelete() {
        if (((MainActivity) getActivity()).deletedNotification != null) {
            if (((MainActivity) getActivity()).deletedNotification.getType().equals(StaticConfiguration.TASK)) {
                for (SchoolNotification n : subjectTasksShown) {
                    if (n.getId().toString().equals(((MainActivity) getActivity()).deletedNotification.getId().toString())) {
                        subjectTasksShown.remove(n);
                        tasksAdapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).deletedNotification = null;
                        break;
                    }
                }
            } else if (((MainActivity) getActivity()).deletedNotification.getType().equals(StaticConfiguration.EXAM)) {
                for (SchoolNotification n : subjectExamsShown) {
                    if (n.getId().toString().equals(((MainActivity) getActivity()).deletedNotification.getId().toString())) {
                        subjectExamsShown.remove(n);
                        examsAdapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).deletedNotification = null;
                        break;
                    }
                }

            }
        } else if (((MainActivity) getActivity()).editedNotification != null) {
            if (((MainActivity) getActivity()).editedNotification.getType().equals(StaticConfiguration.TASK)) {
                for (int i = 0; i < subjectTasksShown.size(); i++) {
                    SchoolNotification n = subjectTasksShown.get(i);
                    if (n.getId().toString().equals(((MainActivity) getActivity()).editedNotification.getId().toString())) {
                        subjectTasksShown.set(i, ((MainActivity) getActivity()).editedNotification);
                        tasksAdapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).editedNotification = null;
                        break;
                    }
                }
            } else if (((MainActivity) getActivity()).editedNotification.getType().equals(StaticConfiguration.EXAM)) {
                for (int i = 0; i < subjectExamsShown.size(); i++) {
                    SchoolNotification n = subjectExamsShown.get(i);
                    if (n.getId().toString().equals(((MainActivity) getActivity()).editedNotification.getId().toString())) {
                        subjectExamsShown.set(i, ((MainActivity) getActivity()).editedNotification);
                        examsAdapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).editedNotification = null;
                        break;
                    }
                }
            }
        } else if (((MainActivity) getActivity()).addedNotification != null) {
            subjectAll.add((((MainActivity) getActivity()).addedNotification));
            clasifyNotifications();
        }

    }

    private void requestNotifications(Integer month) {
        new notificationsTask(token, month).execute();

    }

    private void requestStudents() {
        new studentsTask(token, subject.getSubjectClass().getId()).execute();
    }

    private void clasifyNotifications() {

        subjectTasks = new ArrayList<>();
        subjectExams = new ArrayList<>();
        for (SchoolNotification n : subjectAll) {
            if (n.getType().equals("TASK"))
                subjectTasks.add(n);
            else if (n.getType().equals("EXAM"))
                subjectExams.add(n);
        }

        Calendar calendar = Calendar.getInstance();
        CalendarDay calendarDay;
        List<CalendarDay> task_events_list = new ArrayList<>();
        List<CalendarDay> exam_events_list = new ArrayList<>();
        subjectTasksShown = new ArrayList<>();
        subjectExamsShown = new ArrayList<>();
        subjectAllShown = new ArrayList<>();
        Date notif_date;
        Calendar cal = Calendar.getInstance();
        Date dayToday = new Date();
        index = 0;
        Boolean indexSet = false;
        Boolean indexTSet = false;
        Boolean indexESet = false;
        for (SchoolNotification notif : subjectAll) {

            notif_date = notif.getDate();
            cal.setTime(notif_date);

            if (((Integer) cal.get(Calendar.MONTH)) == calendar_mv.getCurrentDate().getMonth()) {
                if (dayToday.compareTo(notif_date) > 0 && !indexSet) {
                    index = subjectAllShown.size();
                    indexSet = true;
                }
                subjectAllShown.add(notif);

                calendar.set(cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                calendarDay = CalendarDay.from(calendar);

                if (notif.getType().equals(StaticConfiguration.TASK)) {
                    task_events_list.add(calendarDay);
                    if (dayToday.compareTo(notif_date) > 0 && !indexTSet) {
                        indexTasks = subjectTasksShown.size();
                        indexTSet = true;
                    }
                    subjectTasksShown.add(notif);
                } else if (notif.getType().equals(StaticConfiguration.EXAM)) {
                    exam_events_list.add(calendarDay);
                    if (dayToday.compareTo(notif_date) > 0 && !indexESet) {
                        indexExams = subjectExams.size();
                        indexESet = true;
                    }
                    subjectExamsShown.add(notif);
                }
            }
        }
        indexOrigin = index;

        List<CalendarDay> today = new ArrayList<>();
        Calendar today_cal = Calendar.getInstance();
        today_cal.set(Calendar.HOUR_OF_DAY, 0);
        calendarDay = CalendarDay.from(today_cal);
        today.add(calendarDay);

        calendar_mv.addDecorator(new DateDecorator(getActivity(), today, true));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.green), task_events_list));
        calendar_mv.addDecorator(new DateDecorator(getActivity(), getColor(getActivity(), R.color.red), exam_events_list));

        LinearLayoutManager tasksLayoutManager = new LinearLayoutManager(getActivity());
        tasksList.setLayoutManager(tasksLayoutManager);
        tasksList.setHasFixedSize(true);
        LinearLayoutManager examsLayoutManager = new LinearLayoutManager(getActivity());
        examsList.setLayoutManager(examsLayoutManager);
        examsList.setHasFixedSize(true);
        LinearLayoutManager allLayoutManager = new LinearLayoutManager(getActivity());
        allList.setLayoutManager(allLayoutManager);
        allList.setHasFixedSize(true);

        tasksAdapter = new NotificationsAdapterSubject(subjectTasksShown, getResources(), SubjectDetailFragment.this);
        examsAdapter = new NotificationsAdapterSubject(subjectExamsShown, getResources(), SubjectDetailFragment.this);
        allAdapter = new NotificationsAdapterSubject(subjectAllShown, getResources(), SubjectDetailFragment.this);

        tasksList.setAdapter(tasksAdapter);
        examsList.setAdapter(examsAdapter);
        allList.setAdapter(allAdapter);
        tasksList.scrollToPosition(indexTasks);
        examsList.scrollToPosition(indexExams);
        allList.scrollToPosition(index);
    }

    private void setListeners() {
        calendar_mv.setOnMonthChangedListener(new OnMonthChangedListener() {

            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                requestNotifications(date.getMonth());
            }
        });

        calendar_mv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selectedDate == date) {
                    calendar_mv.clearSelection();
                    selectedDate = null;

                    tasksAdapter = new NotificationsAdapterSubject(subjectTasksShown, getResources(), SubjectDetailFragment.this);
                    examsAdapter = new NotificationsAdapterSubject(subjectExamsShown, getResources(), SubjectDetailFragment.this);
                    allAdapter = new NotificationsAdapterSubject(subjectAllShown, getResources(), SubjectDetailFragment.this);

                    tasksList.setAdapter(tasksAdapter);
                    examsList.setAdapter(examsAdapter);
                    allList.setAdapter(allAdapter);
                    tasksList.scrollToPosition(indexTasks);
                    examsList.scrollToPosition(indexExams);
                    allList.scrollToPosition(index);

                } else {
                    selectedDate = date;
                    ArrayList<SchoolNotification> subjectTasksDay = new ArrayList<>();
                    ArrayList<SchoolNotification> subjectExamsDay = new ArrayList<>();
                    ArrayList<SchoolNotification> subjectAllDay = new ArrayList<>();
                    Date notif_date;
                    Calendar cal = Calendar.getInstance();
                    CalendarDay calendarDay;

                    for (SchoolNotification notif : subjectAll) {
                        notif_date = notif.getDate();
                        cal.setTime(notif_date);

                        calendarDay = CalendarDay.from(cal);

                        if (date.getYear() == calendarDay.getYear() &&
                                date.getMonth() == calendarDay.getMonth() &&
                                date.getDay() == calendarDay.getDay()) {
                            subjectAllDay.add(notif);
                            if (notif.getType().equals(StaticConfiguration.TASK)) {
                                subjectTasksDay.add(notif);
                            } else if (notif.getType().equals(StaticConfiguration.EXAM)) {
                                subjectExamsDay.add(notif);
                            }
                        }
                    }

                    tasksAdapter = new NotificationsAdapterSubject(subjectTasksDay, getResources(), SubjectDetailFragment.this);
                    examsAdapter = new NotificationsAdapterSubject(subjectExamsDay, getResources(), SubjectDetailFragment.this);
                    allAdapter = new NotificationsAdapterSubject(subjectAllDay, getResources(), SubjectDetailFragment.this);

                    tasksList.setAdapter(tasksAdapter);
                    examsList.setAdapter(examsAdapter);
                    allList.setAdapter(allAdapter);
                }

            }
        });

        newTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NewNotificationFragment fragment = NewNotificationFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putBoolean("new_task", true);
                bundle.putParcelable("subject", subject);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        newExam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NewNotificationFragment fragment = NewNotificationFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putBoolean("new_exam", true);
                bundle.putParcelable("subject", subject);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
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

    @Override
    public void onStudentItemClick(Student student) {
        StudentDetailFragment detailFragment = StudentDetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("student", student);
        bundle.putParcelable("subject", subject);
        detailFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, detailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onChatItemClick(Student student) {
        new studentsParentsTask(token, student.getId()).execute();
    }

    @Override
    public void onAbsenceItemClick(Student student) {
        NewNotificationFragment fragment = NewNotificationFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("student", student);
        bundle.putParcelable("subject", subject);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void chatWithParent(User parent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userTo", parent);

        ChatFragment chat = ChatFragment.newInstance();
        chat.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, chat, "");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

private class studentsTask extends AsyncTask<Void, Void, JSONArray> {

    JSONObject token;
    Integer id;

    public studentsTask(JSONObject token, Integer id) {
        if (token != null) {
            this.token = token;
            this.id = id;
        }
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        JSONArray studentsJSON = null;
        try {
            JSONObject response = NetworkUtils.schoolClass(token, id);
            studentsJSON = response.getJSONArray("students");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentsJSON;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        try {
            students = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                Student student = new Student(obj.getInt("id"), obj.getString("first_name"), obj.getString("last_name"));
                students.add(student);
            }
            Collections.sort(students, new StudentsComparator());

            setStudents();

            if (!loadedCacheStudents) {
                loadedCacheStudents = true;
                saveCacheStudents();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    private void setStudents() {
        LinearLayoutManager studentsLayoutManager = new LinearLayoutManager(getActivity());
        studentsList.setLayoutManager(studentsLayoutManager);
        studentsList.setHasFixedSize(true);
        studentsAdapter = new StudentsAdapter(students, SubjectDetailFragment.this);
        studentsList.setAdapter(studentsAdapter);
    }

private class studentsParentsTask extends AsyncTask<Void, Void, JSONArray> {

    JSONObject token;
    Integer id;

    public studentsParentsTask(JSONObject token, Integer id) {
        if (token != null) {
            this.token = token;
            this.id = id;
        }
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        JSONArray response = null;
        try {
            response = NetworkUtils.schoolUserParents(token, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(JSONArray response) {
        ArrayList<User> parents = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject parentJson = (JSONObject) response.get(i);
                User user = new User(parentJson.getInt("id"), parentJson.getString("first_name"), parentJson.getString("last_name"));
                parents.add(user);
            }

            if (parents.size() > 1) {
                SelectParentFragment dialog = SelectParentFragment.newInstance(parents);
                dialog.show(getActivity().getSupportFragmentManager(), "SelectParentFragment");
            } else {
                chatWithParent(parents.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

private class notificationsTask extends AsyncTask<Void, Void, Boolean> {

    JSONObject token;
    Integer month;
    ArrayList<SchoolNotification> requestedNotifications = new ArrayList<>();

    public notificationsTask(JSONObject token, Integer month) {
        this.token = token;
        this.month = month;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean result = false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
            Date[] range = Utils.getDateRange(month);

            String begin = dateFormat.format(range[0]);
            String end = dateFormat.format(range[1]);

            JSONArray response = NetworkUtils.schoolNotifications(token, null, null, begin, end, StaticConfiguration.ORDER_DATE_DESC, subject.getId(), subject.getSubjectClass().getId(), null);
            requestedNotifications = Utils.parseNotificationsResponse(response);
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean response) {
        if (response) {
            subjectAllShown = requestedNotifications;
            subjectAll = subjectAllShown;
            clasifyNotifications();
            if (!loadedCacheNotifications) {
                loadedCacheNotifications = true;
                saveCacheNotifications();
            }


        }
        pbLoading.setVisibility(View.INVISIBLE);
    }
}


}

