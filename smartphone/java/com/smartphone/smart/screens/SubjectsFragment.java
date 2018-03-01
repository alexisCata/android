package com.cathedralsw.schoolparent.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.Chat;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.classes.Subject;
import com.cathedralsw.schoolparent.utilities.DBHelper;
import com.cathedralsw.schoolparent.utilities.NetworkUtils;
import com.cathedralsw.schoolparent.utilities.SubjectsAdapter;
import com.cathedralsw.schoolparent.utilities.SubjectsComparator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by alexis on 27/09/17.
 */

public class SubjectsFragment extends Fragment {

    private ArrayList<SchoolNotification> notifications = new ArrayList();
    private ArrayList<Subject> subjects = new ArrayList();
    private ListView lvSubjects;
    private SubjectsAdapter adapter;
    private ProgressBar pbLoading;
    private JSONObject token;
    private Integer studentId = 0;
    private DBHelper schoolDB;


    public static SubjectsFragment newInstance() {
        SubjectsFragment fragment = new SubjectsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        view = initialize(view);

        return view;
    }


    private View initialize(View view) {

        getUserData();

        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        lvSubjects = (ListView) view.findViewById(R.id.list_subjects);

        getCacheData();

        adapter = new SubjectsAdapter(getActivity(), subjects);
        lvSubjects.setAdapter(adapter);

        new dashboardTask(token, studentId).execute();

        return view;
    }

    private void getUserData() {

        token = ((MainActivity) getActivity()).getToken();
        studentId = ((MainActivity) getActivity()).getStudentId();

    }

    private void saveCacheData() {
        schoolDB.deleteSubjects();
        for (Subject s : subjects) {
            schoolDB.saveSubject(s);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        subjects = schoolDB.getSubjects();
    }

    private void setListeners() {
        lvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjects.get(position);
//                ArrayList<SchoolNotification> subject_tasks = new ArrayList<>();
//                ArrayList<SchoolNotification> subject_exams = new ArrayList<>();
//
//                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.DATE, -1);
//                Date yesterday = cal.getTime();

//                for (int i = 0; i < notifications.size(); i++) {
//                    SchoolNotification n = notifications.get(i);
//                    if (n.getType().equals("TASK") && n.getSubject().getId() == subject.getId()) {// && n.getDate().after(yesterday)) {
//                        subject_tasks.add(n);
//                    } else if (n.getType().equals("EXAM") && n.getSubject().getId() == subject.getId()) {// && n.getDate().after(yesterday)) {
//                        subject_exams.add(n);
//                    }
//
//                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("subject", subject);
//                bundle.putParcelableArrayList("tasks", subject_tasks);
//                bundle.putParcelableArrayList("exams", subject_exams);

                SubjectDetailFragment detailFragment = new SubjectDetailFragment();
                detailFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, detailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        });
    }

    public class dashboardTask extends AsyncTask<Boolean, Void, Boolean> {
        JSONObject token;
        Integer studentId;

        public dashboardTask(JSONObject token, Integer studentId) {
            this.token = token;
            this.studentId = studentId;

        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {

                JSONObject response = NetworkUtils.schoolGetUserDashboard(token, studentId);

                JSONArray jsonSubjects = (JSONArray) response.get("subjects");

                for (int i = 0; i < jsonSubjects.length(); i++) {
                    Subject subject = new Subject((JSONObject) jsonSubjects.get(i));
                    subjects.add(subject);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
            subjects = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            Collections.sort(subjects, new SubjectsComparator());

            saveCacheData();

            adapter = new SubjectsAdapter(getActivity(), subjects);
            lvSubjects.setAdapter(adapter);

            setListeners();

            pbLoading.setVisibility(View.INVISIBLE);
        }
    }
}
