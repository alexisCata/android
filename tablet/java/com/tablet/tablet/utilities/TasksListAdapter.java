package com.cathedralsw.schoolteacher.utilities;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.classes.TasksList;

import java.util.ArrayList;

import static com.cathedralsw.schoolteacher.utilities.Utils.formatDateDayMonth;

/**
 * Created by alexis on 18/10/17.
 */

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.NotificationsViewHolder> implements TaskAdapter.ListItemClickListener{

    private TaskAdapter.ListItemClickListener mOnClickListener;
    private ArrayList<TasksList> mTasks;
    private Context context;


    @Override
    public void onListItemClick(SchoolNotification notification) {
        int a = 0;
    }

    public TasksListAdapter(Context context, ArrayList<TasksList> tasks, TaskAdapter.ListItemClickListener listener) {
        mTasks = tasks;
        this.context = context;
        mOnClickListener = listener;

    }


    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.tasks_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NotificationsViewHolder viewHolder = new NotificationsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, int position) {

        TasksList tasksList = mTasks.get(position);

        holder.mNotificationDate.setText(formatDateDayMonth(tasksList.getDate()));

        initTasksManager(holder.rvTasks, tasksList.getTasks(), mOnClickListener);
    }

    private void initTasksManager(RecyclerView rv_tasks, ArrayList<SchoolNotification> tasks, TaskAdapter.ListItemClickListener listener) {
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_tasks.setLayoutManager(manager);
        rv_tasks.setHasFixedSize(true);
//        rv_tasks.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                int a = 0;
//                return true;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                int a = 0;
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
        TaskAdapter taskAdapter = new TaskAdapter(tasks, listener);
        rv_tasks.setAdapter(taskAdapter);
    }

    public TasksList getItem(Integer position) {
        return mTasks.get(position);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    class NotificationsViewHolder extends RecyclerView.ViewHolder{

            RecyclerView rvTasks;
            TextView mNotificationDate;


        public NotificationsViewHolder(View itemView) {
            super(itemView);

            mNotificationDate = (TextView) itemView.findViewById(R.id.tv_tasks_date);
            rvTasks = (RecyclerView) itemView.findViewById(R.id.rv_tasks);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            if (mOnClickListener != null) {
//                int clickedPosition = getAdapterPosition();
////                mOnClickListener.onListItemClick(clickedPosition, mNotifications.get(clickedPosition));
//                mOnClickListener.onListItemClick();
//            }
//        }

    }
}
