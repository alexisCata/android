package com.cathedralsw.schoolteacher.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Chat;
import com.cathedralsw.schoolteacher.classes.DBHelper;
import com.cathedralsw.schoolteacher.classes.User;
import com.cathedralsw.schoolteacher.conf.StaticConfiguration;
import com.cathedralsw.schoolteacher.utilities.ChatsAdapter;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexis on 27/09/17.
 */

public class ChatsFragment extends Fragment implements ChatsAdapter.ListItemClickListener {

    private static String CHAT_TAG = StaticConfiguration.CHAT_TAG;
    private static String USERS_TAG = StaticConfiguration.USERS_TAG;
    private JSONObject token;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Chat> chats = new ArrayList<>();
    private ChatsAdapter adapter;
    private ImageButton btnNewChat;
    private RecyclerView chatsList;
    private ProgressBar progressBar;
    private DBHelper schoolDB;

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //TODO search chat
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        getCacheData();

        chatsList = (RecyclerView) view.findViewById(R.id.list_chats);
        btnNewChat = (ImageButton) view.findViewById(R.id.btn_new_chat);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading_chats);

        LinearLayoutManager tasksLayoutManager = new LinearLayoutManager(getActivity());
        chatsList.setLayoutManager(tasksLayoutManager);
        chatsList.setHasFixedSize(true);
        adapter = new ChatsAdapter(getActivity(), chats, ChatsFragment.this);
        chatsList.setAdapter(adapter);

        getToken();
        //todo some fragments saveinstance check
        requestNewChats();

        setListeners();

        return view;
    }

    private void getToken() {
        token = ((MainActivity) getActivity()).getToken();
    }

    private void saveCacheData() {
        schoolDB.deleteChats();
        for (Chat c : chats) {
            schoolDB.saveChat(c);
        }
    }

    private void getCacheData() {
        schoolDB = new DBHelper(getContext());
        chats = schoolDB.getChats();
    }

    private void setListeners() {
        btnNewChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("users", users);

                UsersListFragment usersList = new UsersListFragment();
                usersList.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, usersList, USERS_TAG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

//        chatsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("userTo", chats.get(position).getUserTo());
//
//                ChatFragment chat = ChatFragment.newInstance();
//                chat.setArguments(bundle);
//
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.frame_layout, chat, CHAT_TAG);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//
//        });
    }

    public void updateAdapter(ArrayList<Chat> newChats) {
        chats = newChats;
        adapter.setChats(newChats);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChatItemClick(Chat chat) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userTo", chat.getUserTo());

        ChatFragment chatF = ChatFragment.newInstance();
        chatF.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, chatF, CHAT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void requestNewChats() {
        new chatsLoadTask(token).execute();
    }

    public class chatsLoadTask extends AsyncTask<Boolean, Void, Boolean> {

        JSONObject token;

        public chatsLoadTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {

            Boolean result = false;

            try {
                JSONArray chatsJSON = NetworkUtils.schoolChats(token);
                chats.clear();
                for (int i = 0; i < chatsJSON.length(); i++) {
                    JSONObject json_chat = (JSONObject) chatsJSON.get(i);
                    Chat chat = new Chat(json_chat);
                    chats.add(chat);
                }

                result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                if (getActivity() != null) {
                    adapter.notifyDataSetChanged();
                    saveCacheData();
                    new usersTask(token).execute();
//                    setBadgeUnread();
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //TODO do it in UserListFragment
    public class usersTask extends AsyncTask<Void, Void, Boolean> {

        JSONObject token;

        public usersTask(JSONObject token) {
            if (token != null) {
                this.token = token;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONArray json_users = NetworkUtils.schoolUsers(token);
//                users = new ArrayList<>();
                ArrayList<User> request_users = new ArrayList<>();
                for (int i = 0; i < json_users.length(); i++) {
                    JSONObject json_user = (JSONObject) json_users.get(i);
                    User user = new User(json_user);
                    request_users.add(user);
                }
                users = new ArrayList<>(request_users);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                setListeners();

                ArrayList<User> usersToRequest = new ArrayList<>();

                for (User u : users) {
                    if (u.getAvatarTimestamp() != null) {
                        usersToRequest.add(u);
                    }
                }

                new getUsersProfileImageTask(token, usersToRequest).execute();
            }
        }
    }

    public class getUsersProfileImageTask extends AsyncTask<Void, Void, Void> {
        JSONObject token;
        ArrayList<User> usersToRequest;

        public getUsersProfileImageTask(JSONObject token, ArrayList<User> usersToRequest) {
            this.token = token;
            this.usersToRequest = usersToRequest;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (User user : usersToRequest) {
                if (getActivity() != null) {
                    File f = new File(getActivity().getFilesDir(), user.getId().toString());
                    Date date = new Date(f.lastModified());

                    if (user.getAvatarTimestamp().after(date)) {
                        try {
                            NetworkUtils.schoolGetUsersProfileImage(token, user.getId(), getActivity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }
    }
}

