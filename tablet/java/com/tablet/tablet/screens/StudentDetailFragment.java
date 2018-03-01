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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.Student;
import com.cathedralsw.schoolteacher.classes.Subject;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.NotificationsAdapter;
import com.cathedralsw.schoolteacher.utilities.NotificationsComparator;
import com.cathedralsw.schoolteacher.utilities.SubjectsComparator;
import com.cathedralsw.schoolteacher.utilities.SubjectsListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;
import static com.cathedralsw.schoolteacher.utilities.Utils.getJsonToken;

/**
 * Created by alexis on 9/10/17.
 */

public class StudentDetailFragment extends Fragment implements NotificationsAdapter.ListItemClickListener {

    private Student student;
    private TeacherSubject subject = null;
    private ArrayList<Subject> subjects = new ArrayList<>();
    private JSONObject token;
    private TextView tvStudent;
    private TextView tvClass;
    private TextView tvAbsences;
    private TextView tvAbsencesMonth;
    private TextView tvTasks;
    private TextView tvTasksTomorrow;
    private TextView tvResults;
    private TextView tvResultsPeriod;
    private ListView lvSubjects;
    private RecyclerView rvAbsences;
    private NotificationsAdapter mAdapter;
    private ProgressBar pbLoading;

    private ArrayList<SchoolNotification> absences = new ArrayList<>();

    public static StudentDetailFragment newInstance() {
        StudentDetailFragment fragment = new StudentDetailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        student = getArguments().getParcelable("student");
        subject = getArguments().getParcelable("subject");

        token = getJsonToken(getActivity().getSharedPreferences("creds", MODE_PRIVATE));

        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        lvSubjects = (ListView) view.findViewById(R.id.lv_subjects);
        rvAbsences = (RecyclerView) view.findViewById(R.id.lv_absences);

        tvStudent = (TextView) view.findViewById(R.id.tv_student);
        tvClass = (TextView) view.findViewById(R.id.tv_class);

        tvAbsences = (TextView) view.findViewById(R.id.tv_absences);
        tvAbsencesMonth = (TextView) view.findViewById(R.id.tv_absences_month);
        tvTasks = (TextView) view.findViewById(R.id.tv_tasks);
        tvTasksTomorrow = (TextView) view.findViewById(R.id.tv_tasks_tomorrow);
        tvResults = (TextView) view.findViewById(R.id.tv_results);
        tvResultsPeriod = (TextView) view.findViewById(R.id.tv_results_period);

        tvStudent.setText(student.toString());
        tvClass.setText(subject.getSubjectClass().toString());

        new studentTask(token, student.getId()).execute();

        return view;
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


    private class studentTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject token;
        Integer id;

        public studentTask(JSONObject token, Integer id) {
            if (token != null) {
                this.token = token;
                this.id = id;
            }
        }

        @Override
        protected void onPreExecute() {
            subjects = new ArrayList<>();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject response = null;
            try {
                response = NetworkUtils.schoolGetUserDashboard(token, id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                new absencesTask(token, student.getId()).execute();

                tvAbsences.setText(response.getString("absences"));
                tvAbsencesMonth.setText(response.getString("absences_this_month"));
                tvTasks.setText(response.getString("tasks"));
                tvTasksTomorrow.setText(response.getString("tasks_for_tomorrow"));
                tvResults.setText(response.getString("score_avg") == "null" ? "-" : response.getString("score_avg"));
                tvResultsPeriod.setText(response.getString("score_avg_period") == "null" ? "-" : response.getString("score_avg_period"));

                JSONArray subjectsJSON = response.getJSONArray("subjects");

                for (int a = 0; a < subjectsJSON.length(); a++) {
                    JSONObject subjectJson = subjectsJSON.getJSONObject(a);
                    Subject subject = new Subject(subjectJson);
                    subjects.add(subject);
                }

                SubjectsListAdapter adapter = new SubjectsListAdapter(getActivity(), subjects);
                lvSubjects.setAdapter(adapter);

                Collections.sort(subjects, new SubjectsComparator());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class absencesTask extends AsyncTask<Void, Void, JSONArray> {

        JSONObject token;
        Integer id;

        public absencesTask(JSONObject token, Integer id) {
            if (token != null) {
                this.token = token;
                this.id = id;
            }
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray absencesJSON = null;
            try {
                absencesJSON = NetworkUtils.schoolNotificationsStudentType(token, id, "ABSENCE");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return absencesJSON;
        }

        @Override
        protected void onPostExecute(JSONArray response) {
            try {
                absences = parseResponse(response);

                Collections.sort(absences, new NotificationsComparator());

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rvAbsences.setLayoutManager(layoutManager);
                rvAbsences.setHasFixedSize(true);
                mAdapter = new NotificationsAdapter(absences, getResources(), StudentDetailFragment.this, true);
                rvAbsences.setAdapter(mAdapter);

                pbLoading.setVisibility(View.INVISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private ArrayList<SchoolNotification> parseResponse(JSONArray reponse) {
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
    }
}

