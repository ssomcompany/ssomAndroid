package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;
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
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.NetworkManager;
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

    public ChattingAdapter(Context context, ArrayList<ChattingItem> itemList){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = NetworkManager.getInstance().getImageLoader();
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
        }else{
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
            holder.initialMessage = (TextView) convertView.findViewById(R.id.initial_message);

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
            holder.chatProfileLayout.setVisibility(item.getMessageTime() != getItem(position - 1).getMessageTime() ? View.VISIBLE : View.INVISIBLE);
            // profile image
            holder.chatProfileImage.setDefaultImageResId(holder.chatProfileLayout.getVisibility() == View.VISIBLE ? R.drawable.profile_img_basic : 0);
            holder.chatProfileImage.setErrorImageResId(holder.chatProfileLayout.getVisibility() == View.VISIBLE ? R.drawable.profile_img_basic : 0);
            holder.chatProfileImage.setImageUrl(holder.chatProfileLayout.getVisibility() == View.VISIBLE ? item.getImageUrl() : null, mImageLoader);
            // profile circle type
            holder.chatProfileCircle.setImageResource(holder.chatProfileLayout.getVisibility() == View.VISIBLE ?
                    (CommonConst.SSOM.equals(item.getSsom()) ? R.drawable.chat_profile_border_green : R.drawable.chat_profile_border_red) : 0);
            holder.receiveMessage.setBackgroundResource(CommonConst.SSOM.equals(item.getSsom()) ? R.drawable.bg_receive_message_green : R.drawable.bg_receive_message_red);
            holder.receiveMessage.setText(item.getMessage());
            // TODO 시간 변경 함수 개발
            holder.receiveTime.setText(String.valueOf(item.getMessageTime()));
        } else {
            holder.receiveMessageLayout.setVisibility(View.GONE);
        }

        /**
         * send layout setting
         */
        if(item.getType() == ChattingItem.MessageType.send) {
            holder.sendMessageLayout.setVisibility(View.VISIBLE);
            holder.sendMessage.setText(item.getMessage());
            // TODO 시간 변경 함수 개발
            holder.sendTime.setText(String.valueOf(item.getMessageTime()));
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

        return convertView;
    }

    public void add(String message) {
        ChattingItem item = new ChattingItem();
        item.setType(ChattingItem.MessageType.send);
        item.setMessage(message);
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
        private TextView initialMessage;
    }
}
