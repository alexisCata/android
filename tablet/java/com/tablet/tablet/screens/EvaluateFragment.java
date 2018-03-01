package com.cathedralsw.schoolteacher.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.StudentScore;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.StudentsComparator;
import com.cathedralsw.schoolteacher.utilities.StudentsEvaluateAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.cathedralsw.schoolteacher.utilities.Utils.eventsType;
import static com.cathedralsw.schoolteacher.utilities.Utils.formatDateDayMonth;

/**
 * Created by alexis on 5/02/18.
 */

public class EvaluateFragment extends Fragment {

    private JSONObject token;
    private TextView tvClass, tvType, tvSubejct, tvDate;
    private SchoolNotification notification;
    private ArrayList<StudentScore> studentsScore = new ArrayList<>();
    private RecyclerView rvStudents;
    private StudentsEvaluateAdapter sAdapter;
    private ProgressBar pbLoading;
//    private ImageButton imgSave;


    public static EvaluateFragment newInstance() {
        EvaluateFragment fragment = new EvaluateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_evaluate, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        token = ((MainActivity) getActivity()).getToken();
        notification = getArguments().getParcelable("notification");

        tvClass = (TextView) view.findViewById(R.id.tv_class);
        tvType = (TextView) view.findViewById(R.id.tv_type);
        tvSubejct = (TextView) view.findViewById(R.id.tv_subject);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
//        imgSave = (ImageButton) view.findViewById(R.id.imgbtn_save);

        tvClass.setText(notification.getTarget_class().getName());
        HashMap<String, String> typeNotifications = eventsType(getResources());
        tvType.setText(typeNotifications.get(notification.getType()));
        tvSubejct.setText(notification.getSubject().getName());
        tvDate.setText(formatDateDayMonth(notification.getDate()));

        rvStudents = (RecyclerView) view.findViewById(R.id.rv_students);
        LinearLayoutManager allLayoutManager = new LinearLayoutManager(getActivity());
        rvStudents.setLayoutManager(allLayoutManager);
        rvStudents.setHasFixedSize(true);

        requestStudents();

        return view;
    }

    private void requestStudents() {
        new studentsScoreTask(token, notification.getId()).execute();
    }

    private void setStudents() {
        sAdapter = new StudentsEvaluateAdapter(token, notification.getId(), studentsScore);
        rvStudents.setAdapter(sAdapter);
    }

//    private void setListeners() {
//        imgSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Mensaje y guardando", Toast.LENGTH_LONG);
//            }
//        });
//    }

    private class studentsScoreTask extends AsyncTask<Void, Void, JSONArray> {

        JSONObject token;
        Integer id;

        public studentsScoreTask(JSONObject token, Integer notificationId) {
            if (token != null) {
                this.token = token;
                this.id = notificationId;
            }
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray response = null;
            try {
                response = NetworkUtils.schoolScoreAll(token, id);
//                studentsJSON = response.getJSONArray("students");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                studentsScore = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    StudentScore student = new StudentScore(obj);
                    studentsScore.add(student);
                }
                Collections.sort(studentsScore, new StudentsComparator());

                setStudents();

                pbLoading.setVisibility(View.INVISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
