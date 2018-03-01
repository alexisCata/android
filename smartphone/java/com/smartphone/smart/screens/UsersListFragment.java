package com.cathedralsw.schoolparent.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cathedralsw.schoolparent.MainActivity;
import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.User;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;
import com.cathedralsw.schoolparent.utilities.UsersAdapter;

import java.util.ArrayList;

/**
 * Created by alexis on 9/10/17.
 */

public class UsersListFragment extends Fragment {

    private ArrayList<User> users;
    private static String USERS_TAG = StaticConfiguration.USERS_TAG;
    private static String TAG = StaticConfiguration.CHAT_TAG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((MainActivity) getActivity()).moveBadge();

        users = getArguments().getParcelableArrayList("users");

        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        UsersAdapter adapter = new UsersAdapter(getActivity(), users);
        // Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.list_users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
//                bundle.putInt("userTo", users.get(position).getId());
//                bundle.putString("userToFirstName", users.get(position).getFirstName());
//                bundle.putString("userToLastName", users.get(position).getLastName());
                bundle.putParcelable("userTo", users.get(position));

                ChatFragment chat = ChatFragment.newInstance();
                chat.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentManager.popBackStackImmediate();
                fragmentTransaction.remove((UsersListFragment) getActivity().getSupportFragmentManager().findFragmentByTag(USERS_TAG));
                fragmentManager.popBackStack();
                fragmentTransaction.replace(R.id.frame_layout, chat, TAG);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();


            }

        });
        return view;
    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        ((MainActivity) getActivity()).undoMoveBadge();
        super.onDestroy();
    }
}
