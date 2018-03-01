package com.cathedralsw.schoolparent.utilities;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cathedralsw.schoolparent.R;
import com.cathedralsw.schoolparent.classes.SchoolNotification;
import com.cathedralsw.schoolparent.conf.StaticConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

import static com.cathedralsw.schoolparent.utilities.Utils.eventsType;
import static com.cathedralsw.schoolparent.utilities.Utils.formatDateDayMonth;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private ListItemClickListener mOnClickListener;
    private ArrayList<SchoolNotification> mNotifications;
    private HashMap<String, String> typeNotifications;

    public interface ListItemClickListener {
        void onListItemClick(SchoolNotification notification);
    }

    public NotificationsAdapter(ArrayList<SchoolNotification> notifications, Resources resources) {
        mNotifications = notifications;
        typeNotifications = eventsType(resources);
    }

    public NotificationsAdapter(ArrayList<SchoolNotification> notifications, Resources resources, ListItemClickListener listener) {
        mNotifications = notifications;
        mOnClickListener = listener;
        typeNotifications = eventsType(resources);
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.notification_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NotificationsViewHolder viewHolder = new NotificationsViewHolder(view);

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
    public void onBindViewHolder(NotificationsViewHolder holder, int position) {

        holder.mNotificationDate.setText(formatDateDayMonth(mNotifications.get(position).getDate()));
        holder.mNotificationType.setText(typeNotifications.get(mNotifications.get(position).getType()));
        holder.mNotificationTitle.setText(mNotifications.get(position).getTitle());
        holder.mNotificationDescription.setText(mNotifications.get(position).getDescription());
        if (mNotifications.get(position).getSubject().getName() != null) {
            holder.mNotificationSubject.setText(mNotifications.get(position).getSubject().getName());
        }

        switch (mNotifications.get(position).getType()) {
            case StaticConfiguration.GENERIC:
                holder.mNotificationType.setBackgroundResource(R.drawable.rectangle_blue);
                break;
            case StaticConfiguration.TASK:
                holder.mNotificationType.setBackgroundResource(R.drawable.rectangle_green);
                break;
            case StaticConfiguration.EXAM:
                holder.mNotificationType.setBackgroundResource(R.drawable.rectangle_red);
                break;
            case StaticConfiguration.ABSENCE:
                holder.mNotificationType.setBackgroundResource(R.drawable.rectangle_black);
                break;
        }


    }

    public SchoolNotification getItem(Integer position){
        return mNotifications.get(position);
    }
    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    /**
     * Cache of the children views for a list item.
     */
    class NotificationsViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        TextView listItemNumberView;
        TextView mNotificationDate;
        TextView mNotificationType;
        TextView mNotificationSubject;
        TextView mNotificationTitle;
        TextView mNotificationDescription;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link NotificationsAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public NotificationsViewHolder(View itemView) {
            super(itemView);

            mNotificationDate = (TextView) itemView.findViewById(R.id.tv_notif_date);
            mNotificationType = (TextView) itemView.findViewById(R.id.tv_notif_type);
            mNotificationSubject = (TextView) itemView.findViewById(R.id.tv_notif_subject);
            mNotificationTitle = (TextView) itemView.findViewById(R.id.tv_notif_tittle);
            mNotificationDescription = (TextView) itemView.findViewById(R.id.tv_notif_description);

            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            listItemNumberView.setText(String.valueOf(listIndex));
        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {

            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onListItemClick(mNotifications.get(clickedPosition));
            }
        }
    }
}
