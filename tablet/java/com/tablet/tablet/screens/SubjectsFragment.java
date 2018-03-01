package com.cathedralsw.schoolteacher.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Class;
import com.cathedralsw.schoolteacher.classes.DBHelper;
import com.cathedralsw.schoolteacher.classes.Subject;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;
import com.cathedralsw.schoolteacher.utilities.ClassComparator;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.SubjectsAdapter;
import com.cathedralsw.schoolteacher.utilities.SubjectsComparator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexis on 27/09/17.
 */

public class SubjectsFragment extends Fragment {

    private JSONObject token;
    private ListView lvSubjects;
    private SubjectsAdapter adapter;
    private ProgressBar pbLoading;
    private ArrayList<TeacherSubject> teacherSubjects = new ArrayList();
    private ArrayList<TeacherSubject> filteredTS = new ArrayList();

    private ArrayList<Subject> subjects = new ArrayList();
    private ArrayList<Class> classes = new ArrayList();

    private Spinner spSubject, spClass;

    private DBHelper schoolDB;
    private Boolean loadedCache = false;


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

        token = ((MainActivity) getActivity()).getToken();

        spClass = (Spinner) view.findViewById(R.id.sp_class);
        spSubject = (Spinner) view.findViewById(R.id.sp_subject);

        getCacheData();

        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        lvSubjects = (ListView) view.findViewById(R.id.list_subjects);

        adapter = new SubjectsAdapter(getActivity(), filteredTS);
        lvSubjects.setAdapter(adapter);

        if (teacherSubjects.isEmpty() || !loadedCache)
            requestSubjects();

        return view;
    }

    private void saveCacheSubjects() {
        schoolDB.deleteSubjects();
        for (Subject s : filteredTS) {
            schoolDB.saveSubject(s);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        filteredTS = schoolDB.getSubjects();
        teacherSubjects = filteredTS;
        setClassesAndSubjects();
    }

    private void setClassesAndSubjects(){
        classifyClassesSubjects();
        if (getActivity()!= null){
            ArrayAdapter sAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, subjects);
            sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSubject.setAdapter(sAdapter);

            ArrayAdapter cAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classes);
            cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClass.setAdapter(cAdapter);
        }
    }
    private void setListeners() {
        lvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeacherSubject subject = filteredTS.get(position);

                Bundle bundle = new Bundle();
                bundle.putParcelable("subject", subject);
//
                SubjectDetailFragment detailFragment = new SubjectDetailFragment();
                detailFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, detailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }

        });

        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                filteredTS = new ArrayList();
                Class clas = (Class) spClass.getSelectedItem();
                if (position > 0) {
                    Subject selectedSubject = (Subject) spSubject.getSelectedItem();

                    for (TeacherSubject ts : teacherSubjects) {
                        if ((ts.getId() == selectedSubject.getId()) && (clas.toString().equals("") || ts.getSubjectClass().getId() == clas.getId()))
                            filteredTS.add(ts);
                    }
                } else {
                    if (clas.toString().equals(""))
                        filteredTS = teacherSubjects;
                    else {
                        for (TeacherSubject ts : teacherSubjects) {
                            if ((ts.getSubjectClass().getId() == clas.getId()))
                                filteredTS.add(ts);
                        }
                    }
                }

                adapter = new SubjectsAdapter(getActivity(), filteredTS);
                lvSubjects.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        spClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                filteredTS = new ArrayList();
                Subject subject = (Subject) spSubject.getSelectedItem();
                if (position > 0) {
                    Class selectedClass = (Class) spClass.getSelectedItem();

                    for (TeacherSubject ts : teacherSubjects) {
                        if ((ts.getSubjectClass().getId() == selectedClass.getId()) && (subject.toString().equals("") || ts.getId() == subject.getId()))
                            filteredTS.add(ts);
                    }
                } else {
                    if (subject.toString().equals(""))
                        filteredTS = teacherSubjects;
                    else {
                        for (TeacherSubject ts : teacherSubjects) {
                            if ((ts.getId() == subject.getId()))
                                filteredTS.add(ts);
                        }
                    }
                }

                adapter = new SubjectsAdapter(getActivity(), filteredTS);
                lvSubjects.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

    }

    private void classifyClassesSubjects() {
        HashMap<Integer, Subject> subjectsH = new HashMap<>();
        HashMap<Integer, Class> classesH = new HashMap<>();

        for (TeacherSubject ts : teacherSubjects) {
            Subject s = new Subject(ts.getId(), ts.getName());
            Class c = new Class(ts.getSubjectClass().getId(), ts.getSubjectClass().getName());
            subjectsH.put(s.getId(), s);
            classesH.put(c.getId(), c);
        }
        subjects = new ArrayList<>(Arrays.asList(new Subject()));
        classes = new ArrayList<>(Arrays.asList(new Class()));

        for (Map.Entry<Integer, Subject> entry : subjectsH.entrySet()) {
            Subject subj = entry.getValue();
            subjects.add(subj);
        }
        for (Map.Entry<Integer, Class> entry : classesH.entrySet()) {
            Class cla = entry.getValue();
            classes.add(cla);
        }
        Collections.sort(subjects, new SubjectsComparator());
        Collections.sort(classes, new ClassComparator());
    }

    private void requestSubjects(){
        new subjectsTask(token).execute();
    }

    public class subjectsTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject token;

        public subjectsTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HashMap<Integer, Class> classHashMap = new HashMap<>();
                JSONArray classesArray = NetworkUtils.schoolClasses(token);
                classes = new ArrayList<>();
                for (int i = 0; i < classesArray.length(); i++) {
                    JSONObject classObject = (JSONObject) classesArray.get(i);
                    Class newClass = new Class(classObject);
                    if (!classHashMap.containsKey(classObject.getInt("id"))) {
                        classHashMap.put(classObject.getInt("id"), newClass);
                        classes.add(newClass);
                    }
                }
                Collections.sort(classes, new ClassComparator());
                teacherSubjects = new ArrayList<>();
                for (Integer key : classHashMap.keySet()) {
                    JSONArray subjectsObj = NetworkUtils.schoolSubjectsClass(token, key);
                    Class teacherClass = classHashMap.get(key);

                    for (int i = 0; i < subjectsObj.length(); i++) {
                        JSONObject sub = (JSONObject) subjectsObj.get(i);
                        TeacherSubject teacherSubject = new TeacherSubject(sub, teacherClass);
                        teacherSubjects.add(teacherSubject);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                loadedCache = true;
                filteredTS = teacherSubjects;
                saveCacheSubjects();
                adapter.notifyDataSetChanged();
                setClassesAndSubjects();
                setListeners();
                pbLoading.setVisibility(View.INVISIBLE);
            }


        }
    }




}
