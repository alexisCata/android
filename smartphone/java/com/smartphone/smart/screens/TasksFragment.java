package com.cathedralsw.schoolparent.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.classes.TasksList;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;
import com.cathedralsw.schoolparent.utilities.NotificationsComparator;
import com.cathedralsw.schoolparent.utilities.NotificationsComparatorReverse;
import com.cathedralsw.schoolparent.utilities.TaskAdapter;
import com.cathedralsw.schoolparent.utilities.TasksListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by alexis on 27/09/17.
 */

public class TasksFragment extends Fragment implements TaskAdapter.ListItemClickListener {

    private ArrayList<SchoolNotification> tasks = new ArrayList();
    private ArrayList<SchoolNotification> exams = new ArrayList();
    private ArrayList<TasksList> tasksLists = new ArrayList<>();
    private ArrayList<TasksList> examsLists = new ArrayList<>();
    private TasksListAdapter tasksAdapter;
    private TasksListAdapter examsAdapter;
    private RecyclerView rvTasksList, rvExamsList;
    private Integer indexT = 0;
    private Integer indexE = 0;

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        ArrayList<SchoolNotification> notif = getArguments().getParcelableArrayList("notifications");

        Collections.sort(notif, new NotificationsComparatorReverse());

        classifyTasks(notif);

        rvTasksList = (RecyclerView) view.findViewById(R.id.rv_tasks);
        rvExamsList = (RecyclerView) view.findViewById(R.id.rv_exams);

        LinearLayoutManager tasksLayoutManager = new LinearLayoutManager(getActivity());
        rvTasksList.setLayoutManager(tasksLayoutManager);
        rvTasksList.setHasFixedSize(true);
        if (tasksLists.size() > 0) {
            tasksAdapter = new TasksListAdapter(getActivity(), tasksLists, TasksFragment.this);
            rvTasksList.setAdapter(tasksAdapter);
            rvTasksList.scrollToPosition(indexT);
        }

        LinearLayoutManager examsLayoutManager = new LinearLayoutManager(getActivity());
        rvExamsList.setLayoutManager(examsLayoutManager);
        rvExamsList.setHasFixedSize(true);
        if (examsLists.size() > 0) {
            examsAdapter = new TasksListAdapter(getActivity(), examsLists, TasksFragment.this);
            rvExamsList.setAdapter(examsAdapter);
            rvExamsList.scrollToPosition(indexE);
        }




        return view;
    }

    private void classifyTasks(ArrayList<SchoolNotification> notif) {

//        tasksLists = new ArrayList<>();
//        notifications = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        for (int i = 0; i < notif.size(); i++) {
            if (notif.get(i).getType().equals(StaticConfiguration.TASK) && notif.get(i).getDate().after(yesterday))
                tasks.add(notif.get(i));
            if (notif.get(i).getType().equals(StaticConfiguration.EXAM) && notif.get(i).getDate().after(yesterday))
                exams.add(notif.get(i));
        }

        if (tasks.size() > 0) {
            TasksList task = new TasksList();

            task.setDate(tasks.get(0).getDate());
            for (int i = 0; i < tasks.size(); i++) {
                SchoolNotification n = tasks.get(i);
                if (n.getDate().equals(task.getDate())) {
                    task.getTasks().add(n);
                } else {
                    if (task.getDate().after(yesterday))
                        indexT = tasksLists.size();
                    tasksLists.add(task);
                    task = new TasksList();
                    task.setDate(n.getDate());
                    task.getTasks().add(n);
                }
            }
            if (task.getDate().after(yesterday))
                indexT = tasksLists.size();
            tasksLists.add(task);
        }

        if (exams.size() > 0) {
            TasksList task = new TasksList();

            task.setDate(exams.get(0).getDate());
            for (int i = 0; i < exams.size(); i++) {
                SchoolNotification n = exams.get(i);
                if (n.getDate().equals(task.getDate())) {
                    task.getTasks().add(n);
                } else {
                    if (task.getDate().after(yesterday))
                        indexE = examsLists.size();
                    examsLists.add(task);
                    task = new TasksList();
                    task.setDate(n.getDate());
                    task.getTasks().add(n);
                }
            }
            if (task.getDate().after(yesterday))
                indexE = examsLists.size();
            examsLists.add(task);
        }


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
}

