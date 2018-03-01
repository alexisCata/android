package com.cathedralsw.schoolteacher.utilities;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Student;
import com.cathedralsw.schoolteacher.classes.StudentScore;

import org.json.JSONObject;

import java.util.ArrayList;


public class StudentsEvaluateAdapter extends RecyclerView.Adapter<StudentsEvaluateAdapter.StudentsViewHolder> {

    private ArrayList<StudentScore> mStudents;
    private Integer notificationId;
    private JSONObject token;

    public StudentsEvaluateAdapter(JSONObject token, Integer id, ArrayList<StudentScore> students) {//, ListItemClickListener listener) {
        this.token = token;
        mStudents = students;
        notificationId = id;
    }

    @Override
    public StudentsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.student_list_evaluate_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        StudentsViewHolder viewHolder = new StudentsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final StudentsViewHolder holder, int position) {
        holder.tvStudent.setText(mStudents.get(position).nameForList());
        if (mStudents.get(position).getScore() != null) {
            holder.etScore.setText(mStudents.get(position).getScore().toString());
            holder.etScore.setBackgroundColor(Color.TRANSPARENT);
        }

        if (mStudents.get(position).getComments() != null) {
            holder.etComments.setText(mStudents.get(position).getComments());
            holder.etComments.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.studentId = mStudents.get(position).getId();
    }

    public Student getItem(Integer position) {
        return mStudents.get(position);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }


    class StudentsViewHolder extends RecyclerView.ViewHolder {

        ImageView imgStudent;
        TextView tvStudent;
        EditText etScore, etComments;
        Integer studentId;

        public StudentsViewHolder(View itemView) {
            super(itemView);

            imgStudent = (ImageView) itemView.findViewById(R.id.img_student);
            tvStudent = (TextView) itemView.findViewById(R.id.tv_student);
            etScore = (EditText) itemView.findViewById(R.id.et_score);
            etComments = (EditText) itemView.findViewById(R.id.et_comments);

            etScore.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        EditText score = (EditText) v;
                        JSONObject data = new JSONObject();
                        try {
                            data.put("score", Float.toString(Float.parseFloat(score.getText().toString())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new setScoreTask(token, notificationId, studentId, data).execute();
                    }
                }
            });

            etComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        EditText comment = (EditText) v;

                        JSONObject data = new JSONObject();
                        try {
                            data.put("comments", comment.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new setScoreTask(token, notificationId, studentId, data).execute();

                    }
                }
            });
        }
    }

    private class setScoreTask extends AsyncTask<Void, Void, Void> {

        JSONObject token;
        Integer notificationID;
        Integer studentId;
        JSONObject data;

        public setScoreTask(JSONObject token, Integer nId, Integer sId, JSONObject data) {
            if (token != null) {
                this.token = token;
                notificationID = nId;
                studentId = sId;
                this.data = data;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                NetworkUtils.schoolSetScore(token, notificationID, studentId, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
