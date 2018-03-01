package com.cathedralsw.schoolteacher.screens;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.MainActivity;
import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.ChatMessage;
import com.cathedralsw.schoolteacher.classes.OnLoadMessagesListener;
import com.cathedralsw.schoolteacher.classes.User;
import com.cathedralsw.schoolteacher.utilities.ChatMessageComparator;
import com.cathedralsw.schoolteacher.utilities.ChatMessagesAdapter;
import com.cathedralsw.schoolteacher.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by alexis on 10/10/17.
 */

public class ChatFragment extends Fragment {
    private ImageButton btnSendMsd;
    private EditText tvMessage;
    private TextView tvUserName;
    private String token;
    private User userFrom, userTo;

    private List<ChatMessage> messages;

    private RecyclerView recyclerView;
    private ChatMessagesAdapter mAdapter;

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        view = initialize(view);

        return view;
    }

    private View initialize(View view) {

        getUserData();

        userTo = getArguments().getParcelable("userTo");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_messages);
        tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        tvUserName.setText(userTo.toString());
        tvMessage = (EditText) view.findViewById(R.id.message_input);
        btnSendMsd = (ImageButton) view.findViewById(R.id.send_button);

        setListeners();

        JSONObject tokenJson = new JSONObject();
        try {
            tokenJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ChatFragment.ChatMessagesTask(tokenJson, userTo.getId(), null).execute();

        return view;
    }

    private void setListeners() {

        btnSendMsd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tvMessage.getText().toString().length() > 0) {
                    sendMessage();
                }

            }
        });

        tvMessage.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (tvMessage.getText().toString().length() > 0) {
                        sendMessage();
                    }
                }
                return false;
            }
        });
    }


    public void getUserData() {

        SharedPreferences prefs = getActivity().getSharedPreferences("creds", MODE_PRIVATE);
        token = prefs.getString("token", null);
        userFrom = new User(prefs.getInt("userId", 0), prefs.getString("first_name", ""), prefs.getString("last_name", ""));
    }


    public void sendMessage() {
        JSONObject data = new JSONObject();
        JSONObject message = new JSONObject();
        try {
            data.put("user_from", userFrom.getId());
            data.put("user_to", userTo.getId());
            data.put("message", tvMessage.getText().toString());
            data.put("timestamps", new java.util.Date());
            message.put("chat_message", data);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            ChatMessage chat_message = new ChatMessage("", userFrom, userTo, tvMessage.getText().toString(), new java.util.Date().toString(), "R");
            messages.add(chat_message);
            mAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().scrollToPosition(messages.size() - 1);

        }
        ((MainActivity) getActivity()).getWebSocketClient().send(message.toString());
        tvMessage.setText("");

    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }

    private List<ChatMessage> parseResponse(JSONArray reponse) {
        List<ChatMessage> messages = new ArrayList<>();
        try {
            for (int i = 0; i < reponse.length(); i++) {
                JSONObject msg = (JSONObject) reponse.get(i);
                JSONObject userFromJson = msg.getJSONObject("user_from");
                JSONObject userToJson = msg.getJSONObject("user_to");
                User userfrom = new User(userFromJson.getInt("id"), userFromJson.getString("first_name"), userFromJson.getString("last_name"));
                User userto = new User(userToJson.getInt("id"), userToJson.getString("first_name"), userToJson.getString("last_name"));
                String align;
                if (userFrom.getId() == userfrom.getId()) {
                    align = "R";
                } else {
                    align = "L";
                }
                ChatMessage new_message = new ChatMessage(msg.getString("id"), userfrom, userto, msg.getString("message"), msg.getString("timestamp"), align);
                messages.add(new_message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }

    public User getUserTo() {
        return userTo;
    }

    public void addMessage(JSONObject message) {
        try {
            ChatMessage chat_message = new ChatMessage("",
                    userTo, userFrom, message.getString("message"), message.getString("timestamp"), "L");
            messages.add(chat_message);
            mAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().scrollToPosition(messages.size() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ChatMessagesTask extends AsyncTask<Void, Void, JSONArray> {

        JSONObject token;
        Integer id;
        String messageId;

        public ChatMessagesTask(JSONObject token, Integer id, String messageId) {
            this.token = token;
            this.id = id;
            this.messageId = messageId;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {

            JSONArray response = null;
            try {
                response = NetworkUtils.schoolChatMessage(token, id, messageId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            if (result != null) {
                messages = parseResponse(result);
            }

            Collections.sort(messages, new ChatMessageComparator());

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new ChatMessagesAdapter(recyclerView, messages, getActivity());
            recyclerView.setAdapter(mAdapter);
            recyclerView.getLayoutManager().scrollToPosition(messages.size() - 1);

            mAdapter.notifyDataSetChanged();
            mAdapter.setLoaded();

            mAdapter.setOnLoadMoreListener(new OnLoadMessagesListener() {
                @Override
                public void onLoadMore() {
                    String id = messages.get(0).getId();

                    new ChatFragment.ChatOldMessagesTask(token, userTo.getId(), id).execute();

                }
            });
        }
    }

    public class ChatOldMessagesTask extends AsyncTask<Void, Void, JSONArray> {

        JSONObject token;
        Integer id;
        String messageId;
        List<ChatMessage> oldMessages = new ArrayList<>();

        public ChatOldMessagesTask(JSONObject token, Integer id, String messageId) {
            this.token = token;
            this.id = id;
            this.messageId = messageId;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {

            JSONArray response = null;
            try {
                response = NetworkUtils.schoolChatMessage(token, id, messageId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            if (result != null && result.length() > 0) {
                oldMessages = parseResponse(result);
                Collections.sort(oldMessages, new ChatMessageComparator());
                if (messages.size() > 1) { //TODO investigate why this if is needed
                    messages.addAll(0, oldMessages);

                    mAdapter.notifyItemRangeInserted(0, oldMessages.size());
                    mAdapter.notifyItemChanged(oldMessages.size());
                    mAdapter.setLoaded();
                }
            }
        }
    }
}
