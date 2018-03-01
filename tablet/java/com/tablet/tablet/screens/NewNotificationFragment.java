package com.cathedralsw.schoolteacher.screens;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Class;
import com.cathedralsw.schoolteacher.classes.IConfirmNotification;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.Student;
import com.cathedralsw.schoolteacher.classes.Subject;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;
import com.cathedralsw.schoolteacher.utilities.ClassComparator;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.StudentsComparator;
import com.cathedralsw.schoolteacher.utilities.SubjectsComparator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.cathedralsw.schoolteacher.conf.StaticConfiguration.ABSENCE;
import static com.cathedralsw.schoolteacher.conf.StaticConfiguration.EXAM;
import static com.cathedralsw.schoolteacher.conf.StaticConfiguration.GENERIC;
import static com.cathedralsw.schoolteacher.conf.StaticConfiguration.TASK;
import static com.cathedralsw.schoolteacher.conf.StaticConfiguration.TYPES;
import static com.cathedralsw.schoolteacher.utilities.Utils.getAppUserId;
import static com.cathedralsw.schoolteacher.utilities.Utils.getJsonToken;

/**
 * Created by alexis on 6/11/17.
 */

public class NewNotificationFragment extends Fragment implements IConfirmNotification {

    private EditText et_notif_date, et_title, et_description;
    private Spinner sp_types, sp_class, sp_subject, sp_student;
    private Button btnSend;
    private ArrayAdapter cAdapter;
    //    private ProgressBar pb_notification;
    private ArrayList<Class> classes = new ArrayList<>();
    private ArrayList<Subject> subjects = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private JSONObject token;
    private Integer userId;
    private Integer notificationId;
    public boolean exit = false;

    //TODO ADD NOTIFICATIONS CREATED OR UPDATED

    public static NewNotificationFragment newInstance() {
        NewNotificationFragment fragment = new NewNotificationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_notification, container, false);

        view = initialize(view);

        return view;
    }

    @Override
    public void onDestroy() {
        exit = true;
        super.onDestroy();
    }

    private View initialize(View view) {


        userId = getAppUserId(getActivity().getSharedPreferences("creds", MODE_PRIVATE));
        token = getJsonToken(getActivity().getSharedPreferences("creds", MODE_PRIVATE));

        et_notif_date = (EditText) view.findViewById(R.id.et_notif_date);
        et_title = (EditText) view.findViewById(R.id.et_notif_title);
        et_description = (EditText) view.findViewById(R.id.et_notif_description);
        sp_types = (Spinner) view.findViewById(R.id.sp_type_notification);
        sp_student = (Spinner) view.findViewById(R.id.sp_student);
        sp_class = (Spinner) view.findViewById(R.id.sp_class);
        sp_subject = (Spinner) view.findViewById(R.id.sp_subject);
        btnSend = (Button) view.findViewById(R.id.btn_notification);
//        pb_notification = (ProgressBar) view.findViewById(R.id.pb_notification);
//        pb_notification.setVisibility(View.INVISIBLE);

        ArrayList<String> types = new ArrayList<>(Arrays.asList(getActivity().getResources().getString(R.string.task),
                getActivity().getResources().getString(R.string.exam), getActivity().getResources().getString(R.string.generic),
                getActivity().getResources().getString(R.string.absence)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_types.setAdapter(adapter);

        requestClasses();

        setListeners();

        return view;
    }

    private void requestClasses(){

        new classesTask(token).execute();

    }

    private void handleNotification(){
        if (getArguments().getBoolean("new_task") == true && getArguments().getParcelable("subject") != null) {
            TeacherSubject subject = getArguments().getParcelable("subject");
            sp_types.setSelection(0);
            classes = new ArrayList<>();
            classes.add(subject.getSubjectClass());
            cAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classes);
            sp_class.setAdapter(cAdapter);

        } else if (getArguments().getBoolean("new_exam") == true && getArguments().getParcelable("subject") != null) {
            TeacherSubject subject = getArguments().getParcelable("subject");
            sp_types.setSelection(1);
            classes = new ArrayList<>();
            classes.add(subject.getSubjectClass());
            cAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classes);
            sp_class.setAdapter(cAdapter);
        }
        //set student absence from subjectDetailFragment
        else if (getArguments().getParcelable("subject") != null && getArguments().getBoolean("new_task") == false && getArguments().getBoolean("new_exam") == false) {
            TeacherSubject subject = getArguments().getParcelable("subject");
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            et_notif_date.setText(format.format(today));
            sp_types.setSelection(3);
            classes = new ArrayList<>();
            classes.add(subject.getSubjectClass());
            cAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classes);
            sp_class.setAdapter(cAdapter);
            et_title.setText(getResources().getString(R.string.absence).concat(" ").concat(subject.toString()));
            et_description.setText(et_title.getText().toString());
        }


        if (getArguments().getBoolean("update")) {
            SchoolNotification notification = getArguments().getParcelable("notification");

            notificationId = notification.getId();

            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            et_notif_date.setText(format.format(notification.getDate()));
            int index = 0;
            switch (notification.getType()) {
                case TASK:
                    index = 0;
                    break;
                case EXAM:
                    index = 1;
                    break;
                case GENERIC:
                    index = 2;
                    break;
                case ABSENCE:
                    index = 3;
                    break;

            }
            sp_types.setSelection(index);
            sp_types.setEnabled(false);
            classes = new ArrayList<>();
            classes.add(notification.getTarget_class());
            cAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classes);
            sp_class.setAdapter(cAdapter);
            sp_class.setEnabled(false);

            subjects = new ArrayList<>();
            subjects.add(notification.getSubject());
            ArrayAdapter sAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, subjects);
            sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_subject.setAdapter(sAdapter);
            sp_subject.setEnabled(false);

            students = new ArrayList<>();
            students.add(notification.getTarget_student());
            ArrayAdapter stAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, students);
            stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_student.setAdapter(stAdapter);
            sp_student.setEnabled(false);

            et_title.setText(notification.getTitle());
            et_description.setText(notification.getDescription());

        }
    }

    private void setListeners() {
        et_notif_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.et_notif_date:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        sp_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new StudentsTask(token, classes.get(position).getId()).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateOnClick();
            }
        });

        et_description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
                }
            }
        });

        et_description.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                    validateOnClick();
                }
                return false;
            }
        });
    }

    private void validateOnClick() {
        String validate = validateNotification();
        if (!validate.equals("")) {
            Toast.makeText(getActivity(), validate, Toast.LENGTH_LONG).show();
        } else {

            ArrayList<String> notification = new ArrayList<>();
            notification.add(et_notif_date.getText().toString().replace(" ", ""));
            notification.add(sp_types.getSelectedItem().toString());
            notification.add(sp_class.getSelectedItem().toString());
            notification.add(sp_subject.getSelectedItem().toString());
            notification.add(sp_student.getSelectedItem().toString());
            notification.add(et_title.getText().toString());
            notification.add(et_description.getText().toString());

            showDialog(notification);
        }
    }

    private void showDialog(ArrayList<String> notification) {


        DialogConfirmationFragment dialog = DialogConfirmationFragment.newInstance(notification);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "DialogConfirmationFragment");
    }

    private String validateNotification() {

        String response = "";
        if (et_notif_date.getText().toString().equals("")) {
            response = getActivity().getResources().getString(R.string.date_error);
        } else if (sp_types.getSelectedItem().toString().equals(getActivity().getResources().getString(R.string.absence))
                && ((Student) sp_student.getSelectedItem()).toString().equals("") ){
            response = getActivity().getResources().getString(R.string.student_error);
        } else if (et_title.getText().toString().equals("")) {
            response = getActivity().getResources().getString(R.string.title_error);
        } else if (et_description.getText().toString().equals("")) {
            response = getActivity().getResources().getString(R.string.description_error);
        }

        return response;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
                et_notif_date.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void resetFields() {
        et_notif_date.setText("");
        sp_types.setSelection(0);
        sp_class.setSelection(0);
        sp_subject.setSelection(0);
        sp_student.setSelection(0);
        et_title.setText("");
        et_description.setText("");

    }

    @Override
    public void onConfirmNotification(Boolean response) {
        if (response) {
            SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat notificationFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String date = "";
            try {
                date = notificationFormat.format(uiFormat.parse(et_notif_date.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String type = TYPES.get(sp_types.getSelectedItemPosition());
            String title = et_title.getText().toString();
            String description = et_description.getText().toString();
            Class cl = (Class) sp_class.getSelectedItem();
            Integer target_class = cl.getId();
            Integer subject = null;
            Integer target_student = null;
            Integer position = 0;
            if (getArguments().getParcelable("student") != null)
                position = -1;

            if (sp_subject.getSelectedItemPosition() > position || getArguments().getBoolean("new_exam") || getArguments().getBoolean("new_task")) {
                Subject sub = (Subject) sp_subject.getSelectedItem();
                subject = sub.getId();
            }
            if (sp_student.getSelectedItemPosition() > position) {
                Student student = (Student) sp_student.getSelectedItem();
                target_student = student.getId();
            }

            JSONObject notification = new JSONObject();
            try {
                notification.put("date", date);
                notification.put("type", type);
                notification.put("title", title);
                notification.put("description", description);
                notification.put("target_class_id", target_class);
                notification.put("subject_id", subject);
                notification.put("target_student_id", target_student);
                notification.put("custom_fields", new JSONObject());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new SendNotificationTask(token, notification).execute();
        }

    }


    public class StudentsTask extends AsyncTask<Void, Void, JSONArray> {
        JSONObject token;
        Integer id;

        public StudentsTask(JSONObject token, Integer id) {
            if (token != null) {
                this.token = token;
                this.id = id;
            }
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            try {
                JSONObject obj;
                JSONObject response = NetworkUtils.schoolClass(token, id);
                JSONArray studentsJSON = response.getJSONArray("students");
                JSONArray subjectsJSON = response.getJSONArray("teachers_subjects");

                if (!getArguments().getBoolean("update")) {
                    students = new ArrayList<>();
                    for (int i = 0; i < studentsJSON.length(); i++) {
                        obj = (JSONObject) studentsJSON.get(i);
                        Student student = new Student(obj.getInt("id"), obj.getString("first_name"), obj.getString("last_name"));
                        students.add(student);
                    }

                    Collections.sort(students, new StudentsComparator());

                    subjects = new ArrayList<>();
                    for (int i = 0; i < subjectsJSON.length(); i++) {
                        obj = (JSONObject) subjectsJSON.get(i);
                        JSONObject teacher = obj.getJSONObject("teacher");
                        if (teacher.getInt("id") == userId) {
                            JSONObject subj = obj.getJSONObject("subject");
                            Subject subject = new Subject(subj.getInt("id"), subj.getString("name"));
                            subjects.add(subject);
                        }
                    }
                    Collections.sort(subjects, new SubjectsComparator());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            if (!getArguments().getBoolean("update") && !exit) {
                subjects.add(0, new Subject());
                ArrayAdapter sAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, subjects);
                sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_subject.setAdapter(sAdapter);

                students.add(0, new Student());
                ArrayAdapter stAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, students);
                stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_student.setAdapter(stAdapter);

                if (getArguments().getParcelable("subject") != null) {
                    TeacherSubject subject = getArguments().getParcelable("subject");
                    subjects = new ArrayList<>();
                    students = new ArrayList<>();
                    subjects.add(subject);
                    sAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, subjects);
                    sp_subject.setAdapter(sAdapter);
                    if (getArguments().getParcelable("student") != null) {
                        Student student = getArguments().getParcelable("student");
                        students.add(student);
                        stAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, students);
                        sp_student.setAdapter(stAdapter);
                        btnSend.performClick();
                    }
                }
            }
        }
    }

    public class SendNotificationTask extends AsyncTask<Void, Void, Object[]> {
        JSONObject token;
        JSONObject data;

        public SendNotificationTask(JSONObject token, JSONObject data) {
            this.token = token;
            this.data = data;

        }

        @Override
        protected void onPreExecute() {
//            pb_notification.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.sending_notif), Toast.LENGTH_LONG).show();
        }

        @Override
        protected Object[] doInBackground(Void... params) {
//            Integer response = 0;
            Object[] response = new Object[2];
            try {
                if (getArguments().getBoolean("update"))
                    response = NetworkUtils.schoolUpdateNotification(token, notificationId, data);
                else
                    response = NetworkUtils.schoolSendNotification(token, data);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Object[] response) {
            if ((Integer) response[0] == 201 || (Integer) response[0] == 200) {
                if ((Integer) response[0] == 201)
                    ((MainActivity) getActivity()).addedNotification = (SchoolNotification) response[1];
                if ((Integer) response[0] == 200)
                    ((MainActivity) getActivity()).editedNotification = (SchoolNotification) response[1];

                if (getArguments().getBoolean("update")) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.notification_updated), Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                    Intent i = new Intent();
                    i.putExtra("date", et_notif_date.getText().toString());
                    i.putExtra("title", et_title.getText().toString());
                    i.putExtra("description", et_description.getText().toString());
                    getTargetFragment().onActivityResult(
                            getTargetRequestCode(),
                            Activity.RESULT_OK,
                            i
                    );
                }else{
                    Integer typeIndex = sp_types.getSelectedItemPosition();
                    resetFields();
                    sp_types.setSelection(typeIndex);

                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.notification_sent), Toast.LENGTH_LONG).show();

                    if (getArguments().getParcelable("student") != null || getArguments().getBoolean("new_exam")
                            || getArguments().getBoolean("new_task")) {

                        getActivity().onBackPressed();
                    }
                }
            } else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.notification_send_error), Toast.LENGTH_LONG).show();
            }
        }
    }


    public class classesTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject token;

        public classesTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            try {

                HashMap<Integer, Class> classHashMap = new HashMap<>();
                JSONArray classesArray = NetworkUtils.schoolClasses(token);
                classes = new ArrayList<>();
                classes.add(new Class());
                for (int i = 0; i < classesArray.length(); i++) {
                    JSONObject classObject = (JSONObject) classesArray.get(i);
                    Class newClass = new Class(classObject);
                    if (!classHashMap.containsKey(classObject.getInt("id"))) {
                        classHashMap.put(classObject.getInt("id"), newClass);
                        classes.add(newClass);
                    }
                }
                Collections.sort(classes, new ClassComparator());

                result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                if (getActivity()!= null){
                    cAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classes);
                    cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_class.setAdapter(cAdapter);

                    handleNotification();
                }
            }
        }
    }

}
