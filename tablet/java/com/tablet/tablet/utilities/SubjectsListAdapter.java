package com.cathedralsw.schoolteacher.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Subject;

import java.util.ArrayList;

/**
 * Created by alexis on 17/01/18.
 */

public class SubjectsListAdapter extends ArrayAdapter<Subject> {
    public SubjectsListAdapter(Context context, ArrayList<Subject> subjects) {
        super(context, 0, subjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Subject subject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subject_list_student_item, parent, false);
        }

        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_subject_name);
        TextView tvResult = (TextView) convertView.findViewById(R.id.tv_subject_result);
        TextView tvTasks = (TextView) convertView.findViewById(R.id.tv_subject_tasks);
        ImageView imgSubject = (ImageView) convertView.findViewById(R.id.img_subject);

        tvSubject.setText(subject.getName());
        tvResult.setText(subject.getScore_avg() == null ? "-" : subject.getScore_avg().toString());
        tvTasks.setText(subject.getTasks() == null ? "-" : subject.getTasks().toString());

        return convertView;
    }

}
