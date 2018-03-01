package com.cathedralsw.schoolteacher.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;

import java.util.ArrayList;
import java.util.HashMap;

import static com.cathedralsw.schoolteacher.utilities.Utils.eventsType;

/**
 * Created by alexis on 4/10/17.
 */

public class NotificationDetailAdapter extends ArrayAdapter<SchoolNotification> {

    private HashMap<String, String> typeNotifications;

    public NotificationDetailAdapter(Context context, ArrayList<SchoolNotification> notifications) {
        super(context, 0, notifications);
        typeNotifications = eventsType(context.getResources());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SchoolNotification notification = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_detail_item, parent, false);
        }
        // Lookup view for data population
//        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_notif_date);
        TextView tvType = (TextView) convertView.findViewById(R.id.tv_notif_type);
        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_notif_subject);
        TextView tvSClass = (TextView) convertView.findViewById(R.id.tv_notif_class);
        TextView tvTittle = (TextView) convertView.findViewById(R.id.tv_notif_tittle);

        // Populate the data into the template view using the data object
//        tvDate.setText(notification.getDate());
        tvType.setText(typeNotifications.get(notification.getType()));
        tvTittle.setText(notification.getTitle());
        tvSubject.setText(notification.getSubject().toString());
        tvSClass.setText(notification.getTarget_class().toString());
        // Return the completed view to render on screen
        return convertView;
    }
}