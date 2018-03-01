package com.cathedralsw.schoolparent.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.Chat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexis on 11/10/17.
 */

public class ChatsAdapter extends ArrayAdapter<Chat> {
    public ChatsAdapter(Context context, ArrayList<Chat> chats) {
        super(context, 0, chats);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Chat chat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_list_item, parent, false);
        }

        ImageView unread = (ImageView) convertView.findViewById(R.id.img_unread);
        ImageView imgChat = (ImageView) convertView.findViewById(R.id.img_user);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tv_user_name);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tv_last_message);

        tvUser.setText(chat.getUserTo().toString());

        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");

        tvDate.setText(dateFormat.format(chat.getTimestamp()).substring(0, 10));
        tvMessage.setText(chat.getLastMessage());

        File f = new File(this.getContext().getFilesDir(), chat.getUserTo().getId().toString());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap profile = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

        if (profile != null)
            imgChat.setImageBitmap(profile);

        try{
            Date timestamp = chat.getTimestamp();
            Date last = chat.getLastRead();

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