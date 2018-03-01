package com.cathedralsw.schoolparent.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;

import java.util.HashMap;

import static com.cathedralsw.schoolparent.utilities.Utils.eventsType;
import static com.cathedralsw.schoolparent.utilities.Utils.formatDateYearMonthDay;
import static com.cathedralsw.schoolparent.utilities.Utils.toCamelCase;

/**
 * Created by alexis on 16/10/17.
 */

public class NotificationDetailFragment extends Fragment {

    private SchoolNotification notification;
    private HashMap<String, String> typeNotifications;

    public static NotificationDetailFragment newInstance() {
        NotificationDetailFragment fragment = new NotificationDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        typeNotifications = eventsType(getResources());

        Bundle bundle = getArguments();
        notification = bundle.getParcelable("notification");

        View view = inflater.inflate(R.layout.fragment_notification_detail, container, false);
        TextView tvType = (TextView) view.findViewById(R.id.tv_detail_type);
        TextView tvSubject = (TextView) view.findViewById(R.id.tv_detail_subject);
        TextView tvDate = (TextView) view.findViewById(R.id.tv_detail_date);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_detail_title);
        TextView tvDescrip = (TextView) view.findViewById(R.id.tv_detail_description);

        tvType.setText(typeNotifications.get(notification.getType()));
        tvSubject.setText(notification.getSubject().getName());

        tvDate.setText(toCamelCase(formatDateYearMonthDay(notification.getDate())));
        tvTitle.setText(notification.getTitle());
        tvDescrip.setText(notification.getDescription());


        return view;
    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }

}
