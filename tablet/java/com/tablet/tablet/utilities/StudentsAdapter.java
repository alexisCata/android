package com.cathedralsw.schoolteacher.utilities;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Student;

import java.util.ArrayList;
import java.util.HashMap;

import static com.cathedralsw.schoolteacher.utilities.Utils.eventsType;


public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder> {

    private ListItemClickListener mOnClickListener;
    private ArrayList<Student> mStudents;

    public interface ListItemClickListener {
        void onStudentItemClick(Student student);
        void onChatItemClick(Student student);
        void onAbsenceItemClick(Student student);
    }


    public StudentsAdapter(ArrayList<Student> students, ListItemClickListener listener) {
        mStudents = students;
        mOnClickListener = listener;
    }

    @Override
    public StudentsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.student_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        StudentsViewHolder viewHolder = new StudentsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StudentsViewHolder holder, int position) {
        final Integer index = position;
        holder.tvStudent.setText(mStudents.get(position).nameForList());
        holder.imgChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onChatItemClick(mStudents.get(index));

            }
        });
        holder.imgAbsence.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onAbsenceItemClick(mStudents.get(index));

            }
        });

    }

    public Student getItem(Integer position){
        return mStudents.get(position);
    }


    @Override
    public int getItemCount() {
        return mStudents.size();
    }


    class StudentsViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        ImageView imgStudent;
        TextView tvStudent;
        ImageButton imgChat, imgAbsence;

        public StudentsViewHolder(View itemView) {
            super(itemView);

            imgStudent = (ImageView) itemView.findViewById(R.id.img_student);
            tvStudent = (TextView) itemView.findViewById(R.id.tv_student);
            imgChat = (ImageButton) itemView.findViewById(R.id.btn_chat_student);
            imgAbsence = (ImageButton) itemView.findViewById(R.id.btn_absence);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onStudentItemClick(mStudents.get(clickedPosition));
            }
        }
    }
}
