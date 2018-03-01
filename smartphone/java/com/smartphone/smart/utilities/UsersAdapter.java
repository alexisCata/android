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
import com.cathedralsw.schoolparent.classes.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by alexis on 9/10/17.
 */

public class UsersAdapter extends ArrayAdapter<User> {
    public UsersAdapter(Context context, ArrayList<User> subjects) {
        super(context, 0, subjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        }

        TextView tvUser = (TextView) convertView.findViewById(R.id.tv_user_name);
        ImageView imgUser = (ImageView) convertView.findViewById(R.id.img_user);
        tvUser.setText(user.toString());

        File f = new File(this.getContext().getFilesDir(), user.getId().toString());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap profile = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

        if (profile != null)
            imgUser.setImageBitmap(profile);

        return convertView;
    }
}