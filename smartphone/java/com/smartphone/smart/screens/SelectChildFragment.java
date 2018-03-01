package com.cathedralsw.schoolparent.screens;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.ISelectedStudent;
import com.cathedralsw.schoolparent.classes.Student;

import java.util.ArrayList;

/**
 * Created by alexis on 5/10/17.
 */

public class SelectChildFragment extends DialogFragment {

    private ArrayList<Student> students;
    private Student selectedStudent;
    private Integer studentId;
    private ISelectedStudent mCallback;

    public static SelectChildFragment newInstance(ArrayList<Student> students, Integer studentId) {
        SelectChildFragment f = new SelectChildFragment();

        Bundle args = new Bundle();
        args.putSerializable("students", students);
        args.putInt("studentId", studentId);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        students = (ArrayList<Student>) getArguments().getSerializable("students");
        studentId = getArguments().getInt("studentId");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_student, null);

        Spinner spin = (Spinner)view.findViewById(R.id.sp_child);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedStudent = students.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });





        ArrayAdapter<Student> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, students);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);


        Integer items = spin.getAdapter().getCount();


        for (int i=0; i<items; i++){
            Student st = (Student) spin.getAdapter().getItem(i);
            if (studentId == st.getId()){
                spin.setSelection(i);
                break;
            }
        }
        builder.setTitle(R.string.select_child)
//                .setMessage(R.string.app_name)
                .setView(view)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallback.onSelectedStudent(selectedStudent.getId(), selectedStudent.getStudentClass().getId());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (ISelectedStudent) context;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
