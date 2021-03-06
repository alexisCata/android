package com.cathedralsw.schoolparent.utilities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;

import java.util.ArrayList;

/**
 * Created by alexis on 18/10/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SchoolNotification> tasks;

    private ListItemClickListener mOnClickListener;

    public TaskAdapter(ArrayList<SchoolNotification> tasks, ListItemClickListener listener) {
        this.tasks = tasks;
        mOnClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(SchoolNotification notification);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        TaskAdapter.TaskViewHolder viewHolder = new TaskAdapter.TaskViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskViewHolder vh = (TaskViewHolder) holder;

        SchoolNotification n = tasks.get(position);
        vh.mTask.setText(n.getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView mTask, mSubject;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mTask = (TextView) itemView.findViewById(R.id.tv_task_tittle);
            mSubject = (TextView) itemView.findViewById(R.id.tv_task_subject);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onListItemClick(tasks.get(clickedPosition));
            }
        }

    }



}