package com.cathedralsw.schoolparent.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.classes.Subject;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;
import com.cathedralsw.schoolparent.utilities.EndlessRecyclerOnScrollListener;
import com.cathedralsw.schoolparent.utilities.NetworkUtils;
import com.cathedralsw.schoolparent.utilities.NotificationsAdapter;
import com.cathedralsw.schoolparent.utilities.SubjectsAdapter;
import com.cathedralsw.schoolparent.utilities.SubjectsComparator;
import com.cathedralsw.schoolparent.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by alexis on 9/10/17.
 */

public class SubjectDetailFragment extends Fragment implements NotificationsAdapter.ListItemClickListener {

    private ArrayList<SchoolNotification> subjectTasks = new ArrayList<>();
    private ArrayList<SchoolNotification> subjectExams = new ArrayList<>();
    private ArrayList<SchoolNotification> requestedTasks = new ArrayList<>();
    private ArrayList<SchoolNotification> requestedExams = new ArrayList<>();
    private Subject subject = null;

    private JSONObject token;
    private Integer studentId = 0;

    private ProgressBar pbLoadingTasks;
    private ProgressBar pbLoadingExams;
    private NotificationsAdapter tasksAdapter, examsAdapter;
    private RecyclerView tasksList, examsList;
    private TextView subjectTitle;
    private Integer indexT, indexE;
    private Integer pagTaskPosition = 0;
    private Integer pagExamPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_subject_detail, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        subject = getArguments().getParcelable("subject");

        pbLoadingTasks = (ProgressBar) view.findViewById(R.id.pb_loading_tasks);
        pbLoadingExams= (ProgressBar) view.findViewById(R.id.pb_loading_exams);
        subjectTitle = (TextView) view.findViewById(R.id.tv_subject_name);
        subjectTitle.setText(subject.getName());
        tasksList = (RecyclerView) view.findViewById(R.id.rv_tasks_list);
        examsList = (RecyclerView) view.findViewById(R.id.rv_exams_list);

        getUserData();

        LinearLayoutManager tasksLayoutManager = new LinearLayoutManager(getActivity());
        tasksList.setLayoutManager(tasksLayoutManager);
        tasksList.setHasFixedSize(true);
        tasksAdapter = new NotificationsAdapter(subjectTasks, getResources(), SubjectDetailFragment.this);
        tasksList.setAdapter(tasksAdapter);
        LinearLayoutManager examsLayoutManager = new LinearLayoutManager(getActivity());
        examsList.setLayoutManager(examsLayoutManager);
        examsList.setHasFixedSize(true);
        examsAdapter = new NotificationsAdapter(subjectExams, getResources(), SubjectDetailFragment.this);
        examsList.setAdapter(examsAdapter);

        getNotifications(null);

        setListeners();

        return view;
    }

    public void setIndexTask(){
        indexT = 0;
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        Date midnight = cal.getTime();

        for (int i = 0; i< subjectTasks.size(); i++){
            SchoolNotification n = subjectTasks.get(i);
            if (n.getDate().after(midnight)){
                indexT = i;
            }else{
                indexT += 1;
                break;
            }
        }

        tasksList.scrollToPosition(indexT);
    }

    public void setIndexExam(){
        indexE = 0;
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        Date midnight = cal.getTime();

        for (int i = 0; i< subjectExams.size(); i++){
            SchoolNotification n = subjectExams.get(i);
            if (n.getDate().after(midnight)){
                indexE = i;
            }else {
                indexE += 1;
                break;
            }
        }

        examsList.scrollToPosition(indexE);
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

    private void getUserData() {

        token = ((MainActivity) getActivity()).getToken();
        studentId = ((MainActivity) getActivity()).getStudentId();

    }

    private void getNotifications(String type) {
        if (type != null)
            new subjectNotificationsTask(token, studentId, subject.getId(), type).execute();
        else{
            new subjectNotificationsTask(token, studentId, subject.getId(), StaticConfiguration.TASK).execute();
            new subjectNotificationsTask(token, studentId, subject.getId(), StaticConfiguration.EXAM).execute();
        }
    }

    private void setListeners(){
        tasksList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                getNotifications(StaticConfiguration.TASK);
            }
        });
        examsList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                getNotifications(StaticConfiguration.EXAM);
            }
        });
    }

    public class subjectNotificationsTask extends AsyncTask<Boolean, Void, Boolean> {
        JSONObject token;
        Integer studentId;
        Integer subjectId;
        String type;
        Integer pagination = 0;

        public subjectNotificationsTask(JSONObject token, Integer studentId, Integer subjectId, String type) {
            this.token = token;
            this.studentId = studentId;
            this.subjectId = subjectId;
            this.type = type;

        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {

                JSONArray response = NetworkUtils.schoolNotifications(token, studentId, 20, pagination, null, null, StaticConfiguration.ORDER_DATE_DESC, subjectId, type);
                if (type.equals(StaticConfiguration.TASK))
                    requestedTasks = Utils.parseNotificationsResponse(response);
                else
                    requestedExams = Utils.parseNotificationsResponse(response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            if (type.equals(StaticConfiguration.TASK)){
                pagination = pagTaskPosition;
                pbLoadingTasks.setVisibility(View.VISIBLE);
            }else{
                pagination = pagExamPosition;
                pbLoadingExams.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (type.equals(StaticConfiguration.TASK)){
                pagTaskPosition += 20;
                subjectTasks.addAll(requestedTasks);
                if (pagination == 0){
                    setIndexTask();
                }
                requestedTasks = new ArrayList<>();
                tasksAdapter.notifyDataSetChanged();
                pbLoadingTasks.setVisibility(View.INVISIBLE);

            }else{
                pagExamPosition += 20;
                subjectExams.addAll(requestedExams);
                if (pagination == 0){
                    setIndexExam();
                }
                requestedExams = new ArrayList<>();
                examsAdapter.notifyDataSetChanged();
                pbLoadingExams.setVisibility(View.INVISIBLE);
            }
        }
    }


}

