package com.cathedralsw.schoolteacher.screens;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.ISelectedParent;
import com.cathedralsw.schoolteacher.classes.User;

import java.util.ArrayList;

/**
 * Created by alexis on 5/10/17.
 */

public class SelectParentFragment extends DialogFragment {

    private ArrayList<User> parents;
    private User selectedParent;
    private ISelectedParent mCallback;

    public static SelectParentFragment newInstance(ArrayList<User> parents) {
        SelectParentFragment f = new SelectParentFragment();

        Bundle args = new Bundle();
        args.putSerializable("parents", parents);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        parents = (ArrayList<User>) getArguments().getSerializable("parents");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_student, null);

        Spinner spin = (Spinner) view.findViewById(R.id.sp_child);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedParent = parents.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        ArrayAdapter<User> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, parents);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(dataAdapter);

        Integer items = spin.getAdapter().getCount();

        builder.setTitle(R.string.select_parent)
//                .setMessage(R.string.app_name)
                .setView(view)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallback.onSelectedParent(selectedParent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (ISelectedParent) context;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
