package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.widget.CircularNetworkImageView;

import java.util.ArrayList;

public class ChattingAdapter extends BaseAdapter {
    private static final String TAG = ChattingAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private Context context;
    private ImageLoader mImageLoader;
    private ChatMessageViewHolder holder;
    private ArrayList<ChattingItem> itemList;
    private ChatRoomItem roomItem;

    public ChattingAdapter(Context context, ChatRoomItem roomItem, ArrayList<ChattingItem> itemList){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = NetworkManager.getInstance().getImageLoader();
        this.roomItem = roomItem;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList == null? 0 : itemList.size();
    }

    @Override
    public ChattingItem getItem(int position) {
        if(getCount() > position) {
            return itemList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return itemList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ssom_chat_message_item, null);

            holder = new ChatMessageViewHolder();
            // received message layout
            holder.receiveMessageLayout = (RelativeLayout) convertView.findViewById(R.id.receive_message_layout);
            holder.chatProfileLayout = (FrameLayout) convertView.findViewById(R.id.chat_profile_layout);
            holder.chatProfileImage = (CircularNetworkImageView) convertView.findViewById(R.id.chat_profile_image);
            holder.chatProfileCircle = (ImageView) convertView.findViewById(R.id.chat_profile_circle);
            holder.receiveMessage = (TextView) convertView.findViewById(R.id.receive_message);
            holder.receiveTime = (TextView) convertView.findViewById(R.id.receive_time);
            // send message layout
            holder.sendMessageLayout = (LinearLayout) convertView.findViewById(R.id.send_message_layout);
            holder.sendMessage = (TextView) convertView.findViewById(R.id.send_message);
            holder.sendTime = (TextView) convertView.findViewById(R.id.send_time);
            // initial message
            holder.initialMessage = (LinearLayout) convertView.findViewById(R.id.initial_message);
            // finish message
            holder.finishMessage = (TextView) convertView.findViewById(R.id.finish_message);

            convertView.setTag(holder);
        } else {
            holder = (ChatMessageViewHolder) convertView.getTag();
        }

        // list view item setting
        ChattingItem item = itemList.get(position);

        /**
         * receive layout setting
         */
        if(item.getType() == ChattingItem.MessageType.receive) {
            holder.receiveMessageLayout.setVisibility(View.VISIBLE);

            if(getItem(position - 1) == null || item.getType() != getItem(position - 1).getType()
                    || !Util.getTimeTextForMessage(item.getMessageTime()).equals(Util.getTimeTextForMessage(getItem(position - 1).getMessageTime()))) {
                holder.chatProfileLayout.setVisibility(View.VISIBLE);
                // profile image
                holder.chatProfileImage.setDefaultImageResId(R.drawable.profile_img_basic);
                holder.chatProfileImage.setErrorImageResId(R.drawable.profile_img_basic);
                holder.chatProfileImage.setImageUrl(roomItem.getImageUrl(), mImageLoader);
                // profile circle type
                holder.chatProfileCircle.setImageResource(CommonConst.SSOM.equals(roomItem.getSsomType()) ? R.drawable.chat_profile_border_green : R.drawable.chat_profile_border_red);
            } else {
                holder.chatProfileLayout.setVisibility(View.INVISIBLE);
            }

            holder.receiveMessage.setBackgroundResource(CommonConst.SSOM.equals(roomItem.getSsomType()) ? R.drawable.bg_receive_message_green : R.drawable.bg_receive_message_red);
            holder.receiveMessage.setText(item.getMessage());
            if(getItem(position + 1) != null && item.getType() == getItem(position + 1).getType()
                    && Util.getTimeTextForMessage(item.getMessageTime()).equals(Util.getTimeTextForMessage(getItem(position + 1).getMessageTime()))) {
                holder.receiveTime.setVisibility(View.GONE);
            } else {
                holder.receiveTime.setVisibility(View.VISIBLE);
                holder.receiveTime.setText(Util.getTimeTextForMessage(item.getMessageTime()));
            }
        } else {
            holder.receiveMessageLayout.setVisibility(View.GONE);
        }

        /**
         * send layout setting
         */
        if(item.getType() == ChattingItem.MessageType.send) {
            holder.sendMessageLayout.setVisibility(View.VISIBLE);
            holder.sendMessage.setText(item.getMessage());
            if(getItem(position + 1) != null && item.getType() == getItem(position + 1).getType()
                    && Util.getTimeTextForMessage(item.getMessageTime()).equals(Util.getTimeTextForMessage(getItem(position + 1).getMessageTime()))) {
                holder.sendTime.setVisibility(View.GONE);
            } else {
                holder.sendTime.setVisibility(View.VISIBLE);
                holder.sendTime.setText(Util.getTimeTextForMessage(item.getMessageTime()));
            }
        } else {
            holder.sendMessageLayout.setVisibility(View.GONE);
        }

        /**
         * initial message layout setting
         */
        if(item.getType() == ChattingItem.MessageType.initial) {
            holder.initialMessage.setVisibility(View.VISIBLE);
        } else {
            holder.initialMessage.setVisibility(View.GONE);
        }

        /**
         * finish message layout setting
         */
        if(item.getType() == ChattingItem.MessageType.finish) {
            holder.finishMessage.setVisibility(View.VISIBLE);
        } else {
            holder.finishMessage.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void add(String message) {
        ChattingItem item = new ChattingItem();
        item.setPostId(roomItem.getPostId());
        item.setType(ChattingItem.MessageType.send);
        item.setMessage(message);
        item.setMessageTime(System.currentTimeMillis());
        itemList.add(item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        itemList.remove(index);
        notifyDataSetChanged();
    }

    private class ChatMessageViewHolder {
        /**
         * Received message layout
         */
        private RelativeLayout receiveMessageLayout;
        private FrameLayout chatProfileLayout;
        private CircularNetworkImageView chatProfileImage;
        private ImageView chatProfileCircle;
        private TextView receiveMessage;
        private TextView receiveTime;

        /**
         * Send message layout
         */
        private LinearLayout sendMessageLayout;
        private TextView sendMessage;
        private TextView sendTime;

        /**
         * Initial ssom message
         */
        private LinearLayout initialMessage;

        /**
         * Finish ssom message
         */
        private TextView finishMessage;
    }
}
