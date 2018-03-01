package com.cathedralsw.schoolparent.screens;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.IClickProfile;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;

/**
 * Created by alexis on 5/10/17.
 */

public class ClickProfileFragment extends DialogFragment {


    private Integer STUDENT = StaticConfiguration.STUDENT;
    private Integer IMAGE = StaticConfiguration.IMAGE;

    private IClickProfile mCallback;

    public static ClickProfileFragment newInstance() {
        ClickProfileFragment f = new ClickProfileFragment();
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.select_student,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mCallback.onSelectedOption(STUDENT);
                            }
                        }
                )
                .setNegativeButton(R.string.select_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mCallback.onSelectedOption(IMAGE);
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (IClickProfile) context;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
