package com.cathedralsw.schoolteacher.screens;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.IConfirmNotification;

import java.util.ArrayList;

/**
 * Created by alexis on 5/10/17.
 */

public class DialogConfirmationFragment extends DialogFragment {

    private ArrayList<String> data;
    private IConfirmNotification mCallback;


    public static DialogConfirmationFragment newInstance(ArrayList<String> notification) {
        DialogConfirmationFragment f = new DialogConfirmationFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("notification", notification);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallback = (IConfirmNotification) getTargetFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        data = getArguments().getStringArrayList("notification");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_notification_confirmation, null);

        TextView tvDate = (TextView) view.findViewById(R.id.tv_detail_date);
        TextView tvType = (TextView) view.findViewById(R.id.tv_detail_type);
        TextView tvClass = (TextView) view.findViewById(R.id.tv_detail_class);
        TextView tvSubject = (TextView) view.findViewById(R.id.tv_detail_subject);
        TextView tvStudent = (TextView) view.findViewById(R.id.tv_detail_student);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_detail_title);
        TextView tvDescrip = (TextView) view.findViewById(R.id.tv_detail_description);

        tvDate.setText(data.get(0));
        tvType.setText(data.get(1));
        tvClass.setText(data.get(2));
        tvSubject.setText(data.get(3));
        tvStudent.setText(data.get(4));
        tvTitle.setText(data.get(5));
        tvDescrip.setText(data.get(6));


        builder.setTitle(R.string.confirm_send)
//                .setMessage(R.string.app_name)
                .setView(view)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallback.onConfirmNotification(true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallback.onConfirmNotification(false);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            mCallback = (IConfirmNotification) getTargetFragment();;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
