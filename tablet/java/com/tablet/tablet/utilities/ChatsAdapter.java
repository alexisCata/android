package com.cathedralsw.schoolteacher.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolteacher.R;
import com.cathedralsw.schoolteacher.classes.Chat;
import com.cathedralsw.schoolteacher.classes.SchoolNotification;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.cathedralsw.schoolteacher.utilities.Utils.eventsType;
import static com.cathedralsw.schoolteacher.utilities.Utils.formatDateDayMonth;

/**
 * Created by alexis on 11/10/17.
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    private ListItemClickListener mOnClickListener;
    private ArrayList<Chat> mChats;
    private Integer homeLayout = null;
    private Context context;

    public interface ListItemClickListener {
        void onChatItemClick(Chat chat);
    }

    public ChatsAdapter(Context cont, ArrayList<Chat> chats, ListItemClickListener listener) {
        context = cont;
        mChats = chats;
        mOnClickListener = listener;

    }

    public ChatsAdapter(Context cont, ArrayList<Chat> chats, ListItemClickListener listener, Integer layout) {
        context = cont;
        mChats = chats;
        mOnClickListener = listener;
        homeLayout = layout;
    }

    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = 0;

        if (homeLayout != null)
             layoutIdForListItem = R.layout.chat_home_list_item;
        else
            layoutIdForListItem = R.layout.chat_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ChatsViewHolder viewHolder = new ChatsViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");

        holder.tvUser.setText(mChats.get(position).getUserTo().toString());
        holder.tvDate.setText(dateFormat.format(mChats.get(position).getTimestamp()).substring(0, 10));
        holder.tvMessage.setText(mChats.get(position).getLastMessage());

        File f = new File(context.getFilesDir(), mChats.get(position).getUserTo().getId().toString());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap profile = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

//        Bitmap img = Bitmap.createScaledBitmap(profile, profile.getWidth() / 10, profile.getHeight() / 10, true);

        if (profile != null)
            holder.imgChat.setImageBitmap(profile);

        try{
            Date timestamp = mChats.get(position).getTimestamp();
            Date last = mChats.get(position).getLastRead();

            if (timestamp.before(last) || timestamp.equals(last)) {
                holder.unread.setVisibility(View.INVISIBLE);
            } else {
                holder.unread.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public Chat getItem(Integer position){
        return mChats.get(position);
    }
    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public void setChats(ArrayList<Chat> chats){
        mChats = chats;
    }

    /**
     * Cache of the children views for a list item.
     */
    class ChatsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {


        ImageView unread;
        ImageView imgChat;
        TextView tvUser;
        TextView tvDate;
        TextView tvMessage;


        public ChatsViewHolder(View itemView) {
            super(itemView);

            unread = (ImageView) itemView.findViewById(R.id.img_unread);
            imgChat = (ImageView) itemView.findViewById(R.id.img_user);
            tvUser = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_last_message);

            itemView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {

            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onChatItemClick(mChats.get(clickedPosition));
            }
        }
    }
}

/*
public class ChatsAdapterOLD extends ArrayAdapter<Chat> {

    Integer homeLayout = null;
    public ChatsAdapter(Context context, ArrayList<Chat> chats) {
        super(context, 0, chats);
    }

    public ChatsAdapter(Context context, ArrayList<Chat> chats, Integer layout) {
        super(context, 0, chats);
        homeLayout = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Chat chat = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            if (homeLayout != null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_home_list_item, parent, false);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_list_item, parent, false);
        }

        ImageView unread = (ImageView) convertView.findViewById(R.id.img_unread);
        ImageView imgChat = (ImageView) convertView.findViewById(R.id.img_user);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tv_user_name);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tv_last_message);

        tvUser.setText(chat.getUserTo().toString());
        tvDate.setText(chat.getTimestamp().substring(0, 10));
        tvMessage.setText(chat.getLastMessage());

        File f = new File(this.getContext().getFilesDir(), chat.getUserTo().getId().toString());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap profile = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

//        Bitmap img = Bitmap.createScaledBitmap(profile, profile.getWidth() / 10, profile.getHeight() / 10, true);

        if (profile != null)
            imgChat.setImageBitmap(profile);

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
            Date timestamp = dateFormat.parse(chat.getTimestamp());
            Date last = dateFormat.parse(chat.getLastRead());

            if (timestamp.before(last) || timestamp.equals(last)) {
                unread.setVisibility(View.INVISIBLE);
            } else {
                unread.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }
}
*/
