package com.cathedralsw.schoolparent.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.utilities.DBHelper;
import com.cathedralsw.schoolparent.utilities.EndlessRecyclerOnScrollListener;
import com.cathedralsw.schoolparent.utilities.NetworkUtils;
import com.cathedralsw.schoolparent.utilities.NotificationsExpandAdapter;
import com.cathedralsw.schoolparent.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//import com.cathedralsw.schoolparent.utilities.DBHelper;


/**
 * Created by alexis on 27/09/17.
 */

public class HomeFragment extends Fragment {

    private ArrayList<SchoolNotification> notifications = new ArrayList();
    private ArrayList<SchoolNotification> requestedNotifications = new ArrayList();
    private NotificationsExpandAdapter mAdapter;
    private RecyclerView rvNotifications;
    private TextView newNotifications;
    private ProgressBar pbLoading;
    private Boolean visibleNewNotifications = false;
    private Integer index = 0;
    private JSONObject token;
    private Integer studentId = 0;
    private Integer pagPosition = 0;
    private DBHelper schoolDB;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_home);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            layout.setOrientation(LinearLayout.HORIZONTAL);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            layout.setOrientation(LinearLayout.VERTICAL);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        getUserData();

        getCacheData();

        rvNotifications = (RecyclerView) view.findViewById(R.id.rv_notifications);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setHasFixedSize(true);
        mAdapter = new NotificationsExpandAdapter(notifications, getResources());
        rvNotifications.setAdapter(mAdapter);

        visibleNewNotifications = getArguments().getBoolean("newNotifications");
        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);

        requestNotifications();

        newNotifications = (TextView) view.findViewById(R.id.tv_new_notifications);
        if (((MainActivity) getActivity()).getAreNewNotifications())
            newNotifications.setVisibility(View.VISIBLE);
        else
            newNotifications.setVisibility(View.INVISIBLE);

        setListeners();

        return view;
    }

    private void saveCacheData() {
        schoolDB.deleteNotificationsHome();
        for (SchoolNotification n : requestedNotifications) {
            schoolDB.saveNotificationHome(n);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        notifications = schoolDB.getNotificationsHome();
    }

    private void getUserData() {
        token = ((MainActivity) getActivity()).getToken();
        studentId = ((MainActivity) getActivity()).getStudentId();
    }

    private void requestNotifications() {
        new paginateNotificationsTask().execute();
    }

    public void visibilityNewNotifications(Boolean notifs) {
        if (notifs) {
            newNotifications.setVisibility(View.VISIBLE);
        } else {
            newNotifications.setVisibility(View.INVISIBLE);
            ((MainActivity) getActivity()).setAreNewNotifications(false);
        }

    }

    private void setListeners() {
        newNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).loadNotifications();
                visibilityNewNotifications(false);
            }
        });

        rvNotifications.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                requestNotifications();
            }
        });

    }

    public class paginateNotificationsTask extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {

                JSONArray response = NetworkUtils.schoolNotifications(token, studentId, 20, pagPosition, null, null, null, null, null);
                requestedNotifications = Utils.parseNotificationsResponse(response);
                pagPosition += 20;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (pagPosition == 20){
                saveCacheData();
                notifications = new ArrayList<>();
            }
            notifications.addAll(requestedNotifications);
            requestedNotifications = new ArrayList<>();
//            mAdapter.notifyDataSetChanged();
            mAdapter = new NotificationsExpandAdapter(notifications, getResources());
            rvNotifications.setAdapter(mAdapter);
            ((MainActivity) getActivity()).enableUIonLoaded();
            pbLoading.setVisibility(View.INVISIBLE);
        }
    }
}
