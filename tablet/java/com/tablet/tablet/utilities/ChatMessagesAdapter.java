package com.cathedralsw.schoolteacher.utilities;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.ChatMessage;
import com.cathedralsw.schoolteacher.classes.OnLoadMessagesListener;

import java.util.List;

/**
 * Created by alexis on 11/10/17.
 */

public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading;
    private Activity activity;

    private List<ChatMessage> messages;

    private int lastVisibleItem;

    private OnLoadMessagesListener onLoadMessagesListener;
    public void setOnLoadMoreListener(OnLoadMessagesListener mOnLoadMoreListener) {
        this.onLoadMessagesListener = mOnLoadMoreListener;
    }

    public ChatMessagesAdapter(RecyclerView recyclerView, List<ChatMessage> messages, Activity activity) {
        this.messages = messages;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (!isLoading && lastVisibleItem == 0 ){
                    if (onLoadMessagesListener != null) {
                        onLoadMessagesListener.onLoadMore();
                    }
                    isLoading=true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.chat_message_item, parent, false);
            return new MessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.chat_messages_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int a = ContextCompat.getColor(activity, R.color.green_chat);

        if (holder instanceof MessageViewHolder) {
            ChatMessage chatMessage = messages.get(position);
            MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            messageViewHolder.message.setText(chatMessage.getMessage());
            messageViewHolder.date.setText(chatMessage.getTimestamp().substring(11,16));
            if (chatMessage.getAlign() == "R"){
                messageViewHolder.layout.setGravity(Gravity.END);
                messageViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.green_chat));
            }else{
                messageViewHolder.layout.setGravity(Gravity.START);
                messageViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.blue_chat));
            }
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    public void setLoaded() {
        isLoading = false;
    }



    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.pb_loading_messages);
        }
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;
        public LinearLayout layout;
        public CardView cardView;

        public MessageViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.tv_message);
            date = (TextView) view.findViewById(R.id.tv_msg_time);
            layout = (LinearLayout) view.findViewById(R.id.layout_message);
            cardView = (CardView) view.findViewById(R.id.cv_chat);
        }
    }

}