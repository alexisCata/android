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
import com.cathedralsw.schoolteacher.classes.TeacherSubject;

import java.util.ArrayList;

/**
 * Created by alexis on 9/10/17.
 */

public class SubjectsAdapter extends ArrayAdapter<TeacherSubject> {
    public SubjectsAdapter(Context context, ArrayList<TeacherSubject> subjects) {
        super(context, 0, subjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TeacherSubject subject = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subject_list_item, parent, false);
        }
        // Lookup view for data population
//        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_notif_date);
        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_subject_name);
        TextView tvClass = (TextView) convertView.findViewById(R.id.tv_subject_class);
        ImageView imgSubject = (ImageView) convertView.findViewById(R.id.img_subject);

        // Populate the data into the template view using the data object
//        tvDate.setText(notification.getDate());
        tvSubject.setText(subject.getName());
        tvClass.setText(subject.getSubjectClass().getName());
        // Return the completed view to render on screen
        return convertView;
    }
}