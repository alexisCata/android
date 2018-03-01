package com.cathedralsw.schoolparent.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.utilities.NotificationDetailAdapter;

import java.util.ArrayList;

import static com.cathedralsw.schoolparent.utilities.Utils.formatDateYearMonthDay;

/**
 * Created by alexis on 3/10/17.
 */

public class NotificationDetailListFragment extends Fragment {

    private TextView events_date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_notification_detail_list, container, false);
        events_date = (TextView) view.findViewById(R.id.tv_notif_detail_date);

        Bundle bundle = getArguments();
        ArrayList<SchoolNotification> notifications = bundle.getParcelableArrayList("data");

        events_date.setText(formatDateYearMonthDay(notifications.get(0).getDate()));

        // Create the adapter to convert the array to views
        NotificationDetailAdapter adapter = new NotificationDetailAdapter(getActivity(), notifications);
        // Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.list_notification_detail);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }



}
