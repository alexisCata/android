package com.cathedralsw.schoolteacher.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Chat;
import com.cathedralsw.schoolteacher.classes.Schedule;

import java.util.ArrayList;

/**
 * Created by alexis on 11/10/17.
 */

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    public ScheduleAdapter(Context context, ArrayList<Schedule> schedule) {
        super(context, 0, schedule);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Schedule schedule = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_list_item, parent, false);
        }

        ImageView imgChat = (ImageView) convertView.findViewById(R.id.img_subject);
        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_subject);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tvClass = (TextView) convertView.findViewById(R.id.tv_class);

        tvSubject.setText(schedule.getTeacherSubject().getName());
        tvTime.setText(schedule.getTime());
        tvClass.setText(schedule.getTeacherSubject().getSubjectClass().getName());

        return convertView;
    }
}