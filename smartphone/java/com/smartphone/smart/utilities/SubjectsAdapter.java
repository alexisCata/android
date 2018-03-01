package com.cathedralsw.schoolparent.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.Subject;

import java.util.ArrayList;

/**
 * Created by alexis on 9/10/17.
 */

public class SubjectsAdapter extends ArrayAdapter<Subject> {
    public SubjectsAdapter(Context context, ArrayList<Subject> subjects) {
        super(context, 0, subjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Subject subject = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subject_list_item, parent, false);
        }

        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_subject_name);
        ImageView imgSubject = (ImageView) convertView.findViewById(R.id.img_subject);
        TextView tvResult = (TextView) convertView.findViewById(R.id.tv_subject_result);
        TextView tvTasks = (TextView) convertView.findViewById(R.id.tv_subject_tasks);


        tvSubject.setText(subject.getName());
        tvResult.setText(subject.getScore_avg() == null ? "-" : subject.getScore_avg().toString());
        tvTasks.setText(subject.getTasks() == null ? "-" : subject.getTasks().toString());

        return convertView;
    }
}