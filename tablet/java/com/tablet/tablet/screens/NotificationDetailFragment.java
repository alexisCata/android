package com.cathedralsw.schoolteacher.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.cathedralsw.schoolteacher.utilities.Utils.eventsType;
import static com.cathedralsw.schoolteacher.utilities.Utils.formatDateYearMonthDay;
import static com.cathedralsw.schoolteacher.utilities.Utils.getJsonToken;
import static com.cathedralsw.schoolteacher.utilities.Utils.toCamelCase;

/**
 * Created by alexis on 16/10/17.
 */

public class NotificationDetailFragment extends Fragment {

    private SchoolNotification notification;
    private HashMap<String, String> typeNotifications;

    private TextView tvType, tvSubject, tvClass, tvStudent, tvDate, tvTitle, tvDescrip;
    private ImageButton btnEdit, btnDelete, btnEvaluate;
    private Integer REQUEST_CODE = 69;

    public static NotificationDetailFragment newInstance() {
        NotificationDetailFragment fragment = new NotificationDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = inflater.inflate(R.layout.fragment_notification_detail, container, false);

        view = init(view);

        setListeners();

        return view;
    }

    private View init(View view) {
        typeNotifications = eventsType(getResources());

        Bundle bundle = getArguments();
        notification = bundle.getParcelable("notification");

        tvType = (TextView) view.findViewById(R.id.tv_detail_type);
        tvSubject = (TextView) view.findViewById(R.id.tv_detail_subject);
        tvClass = (TextView) view.findViewById(R.id.tv_detail_class);
        tvStudent = (TextView) view.findViewById(R.id.tv_detail_student);
        tvDate = (TextView) view.findViewById(R.id.tv_detail_date);
        tvTitle = (TextView) view.findViewById(R.id.tv_detail_title);
        tvDescrip = (TextView) view.findViewById(R.id.tv_detail_description);

        btnEdit = (ImageButton) view.findViewById(R.id.btn_update);
        btnDelete = (ImageButton) view.findViewById(R.id.btn_delete);
        btnEvaluate = (ImageButton) view.findViewById(R.id.btn_evaluate);

        tvType.setText(typeNotifications.get(notification.getType()));
        tvSubject.setText(notification.getSubject().getName());
        tvClass.setText(notification.getTarget_class().toString());
        tvStudent.setText(notification.getTarget_student().toString());

        tvDate.setText(toCamelCase(formatDateYearMonthDay(notification.getDate())));
        tvTitle.setText(notification.getTitle());
        tvDescrip.setText(notification.getDescription());

        if (notification.getType().equals(StaticConfiguration.TASK) ||
                notification.getType().equals(StaticConfiguration.EXAM))
            btnEvaluate.setVisibility(View.VISIBLE);
        else
            btnEvaluate.setVisibility(View.INVISIBLE);

        return view;
    }


    private void setListeners() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle(getActivity().getResources().getString(R.string.delete_notification))
                        .setMessage(getActivity().getResources().getString(R.string.delete_notification))
                        .setPositiveButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) getActivity()).deletedNotification = notification;
                                new deleteTask(getJsonToken(getActivity().getSharedPreferences("creds", MODE_PRIVATE))).execute();
                            }

                        })
                        .setNegativeButton(getActivity().getResources().getString(R.string.cancel), null)
                        .show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle(getActivity().getResources().getString(R.string.delete_notification))
                        .setMessage(getActivity().getResources().getString(R.string.edit_notification))
                        .setPositiveButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NewNotificationFragment fragment = NewNotificationFragment.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("update", true);
                                bundle.putParcelable("notification", notification);
                                fragment.setArguments(bundle);


                                fragment.setTargetFragment(NotificationDetailFragment.this, REQUEST_CODE);

                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

//                                getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

                            }

                        })
                        .setNegativeButton(getActivity().getResources().getString(R.string.cancel), null)
                        .show();
            }
        });
// TODO evaluate
        btnEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = "";
                if (notification.getType().equals(StaticConfiguration.TASK))
                    question = getActivity().getResources().getString(R.string.evaluate_task);
                else
                    question = getActivity().getResources().getString(R.string.evaluate_exam);
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle(getActivity().getResources().getString(R.string.delete_notification))
                        .setMessage(question)
                        .setPositiveButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EvaluateFragment fragment = EvaluateFragment.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("notification", notification);
                                fragment.setArguments(bundle);

                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                            }

                        })
                        .setNegativeButton(getActivity().getResources().getString(R.string.cancel), null)
                        .show();

            }
        });
    }


    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            Bundle extras = data.getExtras();
            tvTitle.setText(extras.getString("title"));
            tvDescrip.setText(extras.getString("description"));
            String date = extras.getString("date");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            notification.setTitle(extras.getString("title"));
            notification.setDescription(extras.getString("description"));
            SimpleDateFormat nFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date nDate;
            try{
                nDate = nFormat.parse(extras.getString("date"));
                notification.setDate(nDate);
            }catch (Exception e){
                e.printStackTrace();
            }

            ((MainActivity) getActivity()).editedNotification = notification;

            try {
                tvDate.setText(toCamelCase(formatDateYearMonthDay(format.parse(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

            getActivity().onBackPressed();

        }
    }

    private class deleteTask extends AsyncTask<Void, Void, Boolean> {

        JSONObject token;

        public deleteTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = false;
            try {
                response = NetworkUtils.schoolDeleteNotification(token, notification.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                ((MainActivity) getActivity()).deleteNotification(notification);

                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.notification_deleted), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            }
        }
    }
}
