package com.cathedralsw.schoolteacher.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.DBHelper;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.TasksList;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;
import com.cathedralsw.schoolteacher.utilities.EndlessRecyclerOnScrollListener;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.TaskAdapter;
import com.cathedralsw.schoolteacher.utilities.TasksListAdapter;
import com.cathedralsw.schoolteacher.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alexis on 27/09/17.
 */

public class TasksFragment extends Fragment implements TaskAdapter.ListItemClickListener {
    private JSONObject token;
    private ProgressBar pbLoadingTasks, pbLoadingExams;
    private ArrayList<SchoolNotification> exams = new ArrayList();
    private ArrayList<SchoolNotification> tasks = new ArrayList();
    private ArrayList<TasksList> tasksLists = new ArrayList<>();
    private ArrayList<TasksList> examsLists = new ArrayList<>();
    private TasksListAdapter tasksAdapter, examsAdapter;
    private RecyclerView rvTasksList, rvExamsList;
    private Integer indexTasks = 0;
    private Integer indexExams = 0;
    private Integer paginationTasks = 0;
    private Integer paginationExams = 0;
    private boolean reload = false;
    private Boolean startE = false;
    private Boolean startT = false;

    private DBHelper schoolDB;
    private Boolean loadedCache = false;

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        token = ((MainActivity) getActivity()).getToken();

        rvTasksList = (RecyclerView) view.findViewById(R.id.rv_tasks);
        rvExamsList = (RecyclerView) view.findViewById(R.id.rv_exams);
        pbLoadingExams = (ProgressBar) view.findViewById(R.id.pb_loading_exams);
        pbLoadingTasks = (ProgressBar) view.findViewById(R.id.pb_loading_tasks);
        //todo REfresh?

//        if (reload) {
////            notif = ((MainActivity) getActivity()).getNotifications();
//        }
//        reload = false;
//        classifyTasks(notif);

        getCacheData();
        classifyTasks(tasks);
        classifyExams(exams);


        LinearLayoutManager tasksLayoutManager = new LinearLayoutManager(getActivity());
        rvTasksList.setLayoutManager(tasksLayoutManager);
        rvTasksList.setHasFixedSize(true);
        tasksAdapter = new TasksListAdapter(getActivity(), tasksLists, TasksFragment.this);
        rvTasksList.setAdapter(tasksAdapter);


        LinearLayoutManager examsLayoutManager = new LinearLayoutManager(getActivity());
        rvExamsList.setLayoutManager(examsLayoutManager);
        rvExamsList.setHasFixedSize(true);
        examsAdapter = new TasksListAdapter(getActivity(), examsLists, TasksFragment.this);
        rvExamsList.setAdapter(examsAdapter);

        requestTasks();
        requestExams();
// TODO EXECUTED TWICE
        setListeners();
        return view;
    }


    private void saveCacheNotificationsTask() {
        schoolDB.deleteNotificationsTasks();
        for (SchoolNotification e : tasks) {
            schoolDB.saveNotificationTaks(e);
        }
    }

    private void saveCacheNotificationsExam() {
        schoolDB.deleteNotificationsExams();
        for (SchoolNotification e : exams) {
            schoolDB.saveNotificationExams(e);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        exams = schoolDB.getNotificationsExams();
        tasks = schoolDB.getNotificationsTasks();
    }


    private void setListeners() {
        rvExamsList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                requestExams();
            }
        });

        rvTasksList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                requestTasks();
            }
        });
    }

    private void classifyTasks(ArrayList<SchoolNotification> requested) {

        if (requested.size() > 0) {

            TasksList task;
            for (SchoolNotification n : requested) {
                if (tasksLists.size() == 0 || !tasksLists.get(tasksLists.size() - 1).getDate().equals(n.getDate())) {
                    task = new TasksList();
                    task.setDate(n.getDate());
                    task.getTasks().add(n);
                    tasksLists.add(task);
                } else if (tasksLists.get(tasksLists.size() - 1).getDate().equals(n.getDate())) {
                    tasksLists.get(tasksLists.size() - 1).getTasks().add(n);
                }
            }
        }
    }

    private void classifyExams(ArrayList<SchoolNotification> requested) {

        if (requested.size() > 0) {
            TasksList exam;

            for (SchoolNotification n : requested) {
                if (examsLists.size() == 0 || !examsLists.get(examsLists.size() - 1).getDate().equals(n.getDate())) {
                    exam = new TasksList();
                    exam.setDate(n.getDate());
                    exam.getTasks().add(n);
                    examsLists.add(exam);
                } else if (examsLists.get(examsLists.size() - 1).getDate().equals(n.getDate())) {
                    examsLists.get(examsLists.size() - 1).getTasks().add(n);
                }
            }
        }
    }

    private void indexTasks() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        Date midnight = cal.getTime();

        for (int i = 0; i < tasksLists.size(); i++) {
            TasksList t = tasksLists.get(i);
            if (t.getDate().after(midnight)) {
                indexTasks = i;
            } else {
                indexTasks -= 1;
                break;
            }
        }
    }

    private void indexExams() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        Date midnight = cal.getTime();

        for (int i = 0; i < examsLists.size(); i++) {
            TasksList t = examsLists.get(i);
            if (t.getDate().after(midnight)) {
                indexExams = i;
            } else {
                indexExams -= 1;
                break;
            }
        }
    }

    @Override
    public void onListItemClick(SchoolNotification notification) {

        reload = true;

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

    private void requestTasks() {

        new notificationsTask(StaticConfiguration.TASK).execute();
    }

    private void requestExams() {

        new notificationsTask(StaticConfiguration.EXAM).execute();
    }

    public class notificationsTask extends AsyncTask<Boolean, Void, Boolean> {

        String type;
        Integer pagination;
        ArrayList<SchoolNotification> requestedNotifications = new ArrayList<>();

        public notificationsTask(String type) {
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {

                if (type.equals(StaticConfiguration.TASK)) {
                    pagination = paginationTasks;
                } else {
                    pagination = paginationExams;
                }

                JSONArray response = NetworkUtils.schoolNotifications(token, 40, pagination, null, null, StaticConfiguration.ORDER_DATE_DESC, null, null, type);
                requestedNotifications = Utils.parseNotificationsResponse(response);
                pagination += 40;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            if (type.equals(StaticConfiguration.TASK))
                pbLoadingTasks.setVisibility(View.VISIBLE);
            else
                pbLoadingExams.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (type.equals(StaticConfiguration.TASK)) {
                tasks.addAll(requestedNotifications);

                classifyTasks(requestedNotifications);
                tasksAdapter.notifyDataSetChanged();

                if (paginationTasks == 0) {
                    indexTasks();
                    rvTasksList.scrollToPosition(indexTasks);
                    saveCacheNotificationsTask();
                }
                paginationTasks = pagination;
                pbLoadingTasks.setVisibility(View.INVISIBLE);
                startT = true;

            } else {
                exams.addAll(requestedNotifications);

                classifyExams(requestedNotifications);
                examsAdapter.notifyDataSetChanged();
                if (paginationExams == 0) {
                    indexExams();
                    rvExamsList.scrollToPosition(indexExams);
                    saveCacheNotificationsExam();
                }
                paginationExams = pagination;
                pbLoadingExams.setVisibility(View.INVISIBLE);
                startE = true;
            }
        }
    }
}

