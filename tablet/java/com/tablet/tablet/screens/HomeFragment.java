package com.cathedralsw.schoolteacher.screens;

import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Chat;
import com.cathedralsw.schoolteacher.classes.Class;
import com.cathedralsw.schoolteacher.classes.DBHelper;
import com.cathedralsw.schoolteacher.classes.Schedule;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.TeacherSubject;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;
import com.cathedralsw.schoolteacher.utilities.ChatsAdapter;
import com.cathedralsw.schoolteacher.utilities.EndlessRecyclerOnScrollListener;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;
import com.cathedralsw.schoolteacher.utilities.NotificationsAdapter;
import com.cathedralsw.schoolteacher.utilities.ScheduleAdapter;
import com.cathedralsw.schoolteacher.utilities.ScheduleComparator;
import com.cathedralsw.schoolteacher.utilities.SwipeController;
import com.cathedralsw.schoolteacher.utilities.SwipeControllerActions;
import com.cathedralsw.schoolteacher.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexis on 27/09/17.
 */

public class HomeFragment extends Fragment implements NotificationsAdapter.ListItemClickListener, ChatsAdapter.ListItemClickListener {

    private static String TAG = StaticConfiguration.CHAT_TAG;
    private JSONObject token;
    private Integer paginationNotifications = 0;
    private ProgressBar pbLoading;
    private ArrayList<SchoolNotification> notifications = new ArrayList();
    private ArrayList<Chat> chats = new ArrayList<>();
    private ArrayList<Schedule> schedule = new ArrayList<>();
    private NotificationsAdapter mAdapter;
    private ChatsAdapter cAdapter;
    private ScheduleAdapter sAdapter;
    private RecyclerView rvNotifications;
    private RecyclerView lvChats;
    private ListView lvSchedule;
    private SwipeController swipeController;
    private DBHelper schoolDB;
    private Boolean loadedCacheNotifications = false;
    private Boolean loadedCacheChats = false;
    private Boolean loadedCacheSchedule = false;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
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

//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_home);
//        switch (getResources().getConfiguration().orientation){
//            case Configuration.ORIENTATION_PORTRAIT:
//                layout.setOrientation(LinearLayout.VERTICAL);
//
//                break;
//            case Configuration.ORIENTATION_LANDSCAPE:
//                layout.setOrientation(LinearLayout.HORIZONTAL);
//                break;
//        }

        view = initialize(view);

        checkIfEditOrDelete();

        return view;
    }

    private void checkIfEditOrDelete() {
        if (((MainActivity) getActivity()).deletedNotification != null) {
            for (SchoolNotification n : notifications) {
                if (n.getId().toString().equals(((MainActivity) getActivity()).deletedNotification.getId().toString())) {
                    notifications.remove(n);
                    mAdapter.notifyDataSetChanged();
                    ((MainActivity) getActivity()).deletedNotification = null;
                    break;
                }
            }
        } else if (((MainActivity) getActivity()).editedNotification != null) {
            for (int i = 0; i < notifications.size(); i++) {
                SchoolNotification n = notifications.get(i);
                if (n.getId().toString().equals(((MainActivity) getActivity()).editedNotification.getId().toString())) {
                    notifications.set(i, ((MainActivity) getActivity()).editedNotification);
                    mAdapter.notifyDataSetChanged();
                    ((MainActivity) getActivity()).editedNotification = null;
                    break;
                }
            }
        }
    }

    private View initialize(View view) {

        getCacheData();

        token = ((MainActivity) getActivity()).getToken();
        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);

        rvNotifications = (RecyclerView) view.findViewById(R.id.rv_notifications);
        lvChats = (RecyclerView) view.findViewById(R.id.lv_chat_msgs);
        lvSchedule = (ListView) view.findViewById(R.id.lv_schedule);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setHasFixedSize(true);
        mAdapter = new NotificationsAdapter(notifications, getResources(), HomeFragment.this);
        rvNotifications.setAdapter(mAdapter);

        if (notifications.isEmpty() || !loadedCacheNotifications)
            requestNotifications();

        sAdapter = new ScheduleAdapter(getActivity(), schedule);
        lvSchedule.setAdapter(sAdapter);

        if (schedule.isEmpty() || !loadedCacheSchedule)
            requestSchedule();

        LinearLayoutManager layoutManagerChats = new LinearLayoutManager(getActivity());
        lvChats.setLayoutManager(layoutManagerChats);
        lvChats.setHasFixedSize(true);

        cAdapter = new ChatsAdapter(getActivity(), chats, HomeFragment.this, R.layout.chat_home_list_item);
        lvChats.setAdapter(cAdapter);

        if (chats.isEmpty() || !loadedCacheChats)
            requestChats();

        swipeController = new SwipeController(getContext(), new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
//                mAdapter.players.remove(position);
//                mAdapter.notifyItemRemoved(position);
//                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(lvChats);

        lvChats.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


        setListeners();

        return view;
    }

    private void saveCacheNotifications(ArrayList<SchoolNotification> notifications) {
        schoolDB.deleteNotificationsHome();
        for (SchoolNotification n : notifications) {
            schoolDB.saveNotificationHome(n);
        }
    }

    private void saveCacheSchedule() {
        schoolDB.deleteSchedule();
        for (Schedule s : schedule) {
            schoolDB.saveSchedule(s);
        }
    }

    private void saveCacheChats() {
        schoolDB.deleteChats();
        for (Chat c : chats) {
            schoolDB.saveChat(c);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        notifications = schoolDB.getNotificationsHome();
        schedule = schoolDB.getSchedule();
        chats = schoolDB.getChats();
    }


    private void setListeners() {
//        lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("userTo", chats.get(position).getUserTo());
//
//                ChatFragment chat = ChatFragment.newInstance();
//                chat.setArguments(bundle);
//
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.frame_layout, chat, TAG);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//
//        });

        lvSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeacherSubject subject = schedule.get(position).getTeacherSubject();
//                ArrayList<SchoolNotification> subject_tasks = new ArrayList<>();
//                ArrayList<SchoolNotification> subject_exams = new ArrayList<>();
                ArrayList<SchoolNotification> subject_all = new ArrayList<>();

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesterday = cal.getTime();

                for (int i = 0; i < notifications.size(); i++) {
                    SchoolNotification n = notifications.get(i);
                    if (n.getSubject().getId() == subject.getId()
                            && n.getTarget_class().getId() == subject.getSubjectClass().getId()
                            && n.getDate().after(yesterday)) {
                        subject_all.add(n);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("subject", subject);
                bundle.putParcelableArrayList("all", subject_all);

                SubjectDetailFragment detailFragment = new SubjectDetailFragment();
                detailFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, detailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        rvNotifications.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                requestNotifications();
            }
        });


    }

    @Override
    public void onListItemClick(SchoolNotification notification) {

        NotificationDetailFragment detailFragment = NotificationDetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("notification", notification);
        detailFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, detailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onChatItemClick(Chat chat) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userTo", chat.getUserTo());

        ChatFragment chatFrag = ChatFragment.newInstance();
        chatFrag.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, chatFrag, TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void requestNotifications() {

        new paginateNotificationsTask(token).execute();
    }

    public void requestChats() {
        new chatsLoadTask(token).execute();
    }

    private void requestSchedule() {
        new scheduleTask(token).execute();
    }

    public void setIndex() {
        Integer index = 0;
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        Date midnight = cal.getTime();

        for (int i = 0; i < notifications.size(); i++) {
            SchoolNotification n = notifications.get(i);
            if (n.getDate().after(midnight)) {
                index = i;
            } else {
                index += 1;
                break;
            }
        }
        rvNotifications.scrollToPosition(index);
    }


    public class paginateNotificationsTask extends AsyncTask<Boolean, Void, Boolean> {

        JSONObject token;
        ArrayList<SchoolNotification> requestedNotifications = new ArrayList();

        public paginateNotificationsTask(JSONObject token) {
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            Boolean result = false;
            try {
                JSONArray response = NetworkUtils.schoolNotifications(token, 20, paginationNotifications, null, null, StaticConfiguration.ORDER_DATE_DESC, null, null, null);
                requestedNotifications = Utils.parseNotificationsResponse(response);
                paginationNotifications += 20;
                result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!loadedCacheNotifications) {
                loadedCacheNotifications = true;
                notifications.clear();
                saveCacheNotifications(requestedNotifications);
            }
            notifications.addAll(requestedNotifications);
            mAdapter.notifyDataSetChanged();
            if (paginationNotifications == 0) {
                setIndex();
            }
            pbLoading.setVisibility(View.INVISIBLE);
        }
    }

    public class chatsLoadTask extends AsyncTask<Boolean, Void, Boolean> {

        JSONObject token;
        ArrayList<Chat> requestedChats = new ArrayList();

        public chatsLoadTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            Boolean result = false;
            try {
                chats.clear();
                JSONArray chatsJSON = NetworkUtils.schoolChats(token);
                for (int i = 0; i < chatsJSON.length(); i++) {
                    JSONObject json_chat = (JSONObject) chatsJSON.get(i);
                    Chat chat = new Chat(json_chat);
                    chats.add(chat);
                }
                result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (!loadedCacheChats)
                    saveCacheChats();
                loadedCacheChats = true;
                cAdapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class scheduleTask extends AsyncTask<Boolean, Void, Boolean> {

        JSONObject token;

        public scheduleTask(JSONObject token) {
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            JSONArray scheduleJSON;
            Boolean result = false;
            try {
                scheduleJSON = NetworkUtils.schoolSchedule(token);

                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                String day = format.format(today).toUpperCase();

                schedule.clear();
                for (int i = 0; i < scheduleJSON.length(); i++) {
                    JSONObject json_schedule = (JSONObject) scheduleJSON.get(i);
                    JSONObject objectJSON, subjectJSON, classJSON;
                    objectJSON = json_schedule.getJSONObject("class_teacher_subject");
                    subjectJSON = objectJSON.getJSONObject("subject");
                    classJSON = objectJSON.getJSONObject("teaches_in");
                    Class inClass = new Class(classJSON);
                    TeacherSubject teacherSubject = new TeacherSubject(subjectJSON, inClass);
                    Schedule sched = new Schedule(json_schedule, teacherSubject);
                    if (day.equals(sched.getDay())) {
                        schedule.add(sched);
                    }
                }
                Collections.sort(schedule, new ScheduleComparator());

                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (!loadedCacheSchedule)
                    saveCacheSchedule();
                loadedCacheSchedule = true;

                sAdapter.notifyDataSetChanged();
            }
        }
    }
}
