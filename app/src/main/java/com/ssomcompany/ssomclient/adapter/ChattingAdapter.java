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
import com.ssomcompany.ssomclient.activity.BaseActivity;
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

    public ChattingAdapter(Context context, ChatRoomItem roomItem){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = NetworkManager.getInstance().getImageLoader();
        this.roomItem = roomItem;
    }

    public ArrayList<ChattingItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<ChattingItem> itemList) {
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
            holder.leftChatProfileLayout = (FrameLayout) convertView.findViewById(R.id.left_chat_profile_layout);
            holder.leftChatProfileImage = (CircularNetworkImageView) convertView.findViewById(R.id.left_chat_profile_image);
            holder.leftChatProfileCircle = (ImageView) convertView.findViewById(R.id.left_chat_profile_circle);
            holder.receiveMessage = (TextView) convertView.findViewById(R.id.receive_message);
            holder.receiveTime = (TextView) convertView.findViewById(R.id.receive_time);
            // send message layout
            holder.sendMessageLayout = (RelativeLayout) convertView.findViewById(R.id.send_message_layout);
            holder.rightChatProfileLayout = (FrameLayout) convertView.findViewById(R.id.right_chat_profile_layout);
            holder.rightChatProfileImage = (CircularNetworkImageView) convertView.findViewById(R.id.right_chat_profile_image);
            holder.rightChatProfileCircle = (ImageView) convertView.findViewById(R.id.right_chat_profile_circle);
            holder.sendMessage = (TextView) convertView.findViewById(R.id.send_message);
            holder.sendTime = (TextView) convertView.findViewById(R.id.send_time);
            // initial message
            holder.initialMessage = (LinearLayout) convertView.findViewById(R.id.initial_message);
            // request message
            holder.requestMessage = (TextView) convertView.findViewById(R.id.request_message);
            // approve message
            holder.approveMessage = (TextView) convertView.findViewById(R.id.approve_message);
            // finish message
            holder.finishMessage = (TextView) convertView.findViewById(R.id.finish_message);

            convertView.setTag(holder);
        } else {
            holder = (ChatMessageViewHolder) convertView.getTag();
        }

        // list view item setting
        ChattingItem item = itemList.get(position);

        if(CommonConst.Chatting.SYSTEM.equals(item.getMsgType())) {
            /**
             * initial message layout setting
             */
            if (item.getStatus() == ChattingItem.MessageType.initial) {
                holder.initialMessage.setVisibility(View.VISIBLE);
                holder.requestMessage.setVisibility(View.GONE);
                holder.approveMessage.setVisibility(View.GONE);
                holder.finishMessage.setVisibility(View.GONE);
                holder.sendMessageLayout.setVisibility(View.GONE);
                holder.receiveMessageLayout.setVisibility(View.GONE);
            } else if (item.getStatus() == ChattingItem.MessageType.finish ||
                    CommonConst.Chatting.MEETING_COMPLETE.equals(item.getMsg())) {
                /**
                 * finish message layout setting
                 */
                holder.initialMessage.setVisibility(View.GONE);
                holder.requestMessage.setVisibility(View.GONE);
                holder.approveMessage.setVisibility(View.GONE);
                holder.finishMessage.setVisibility(View.VISIBLE);
                holder.sendMessageLayout.setVisibility(View.GONE);
                holder.receiveMessageLayout.setVisibility(View.GONE);
            } else if (item.getStatus() == ChattingItem.MessageType.request ||
                    CommonConst.Chatting.MEETING_REQUEST.equals(item.getMsg())) {
                /**
                 * request message setting
                 */
                holder.initialMessage.setVisibility(View.GONE);
                holder.requestMessage.setVisibility(View.VISIBLE);
                holder.requestMessage.setText(((BaseActivity) context).getUserId().equals(item.getFromUserId()) ?
                        R.string.chat_message_request_sent : R.string.chat_message_request_received);
                holder.approveMessage.setVisibility(View.GONE);
                holder.finishMessage.setVisibility(View.GONE);
                holder.sendMessageLayout.setVisibility(View.GONE);
                holder.receiveMessageLayout.setVisibility(View.GONE);
            } else if (item.getStatus() == ChattingItem.MessageType.approve ||
                    CommonConst.Chatting.MEETING_APPROVE.equals(item.getMsg())) {
                /**
                 * approve message setting
                 */
                holder.initialMessage.setVisibility(View.GONE);
                holder.requestMessage.setVisibility(View.GONE);
                holder.approveMessage.setVisibility(View.VISIBLE);
                holder.finishMessage.setVisibility(View.GONE);
                holder.sendMessageLayout.setVisibility(View.GONE);
                holder.receiveMessageLayout.setVisibility(View.GONE);
            }
        } else {
            holder.initialMessage.setVisibility(View.GONE);
            holder.finishMessage.setVisibility(View.GONE);
            holder.requestMessage.setVisibility(View.GONE);
            holder.approveMessage.setVisibility(View.GONE);

            /**
             * receive layout setting
             */
            if(!((BaseActivity) context).getUserId().equals(item.getFromUserId())) {
                holder.sendMessageLayout.setVisibility(View.GONE);
                holder.receiveMessageLayout.setVisibility(View.VISIBLE);

                if(position == 0 || getItem(position - 1) == null || !item.getFromUserId().equals(getItem(position - 1).getFromUserId())
                        || !Util.isSameTimeBetweenTwoTimes(item.getTimestamp(), getItem(position - 1).getTimestamp())) {
                    holder.leftChatProfileLayout.setVisibility(View.VISIBLE);
                    // profile image
                    holder.leftChatProfileImage.setDefaultImageResId(R.drawable.profile_img_basic);
                    holder.leftChatProfileImage.setErrorImageResId(R.drawable.profile_img_basic);
                    holder.leftChatProfileImage.setImageUrl(((BaseActivity) context).getUserId().equals(roomItem.getOwnerId()) ?
                            roomItem.getParticipantImageUrl() : roomItem.getOwnerImageUrl(), mImageLoader);
                    // profile circle type
                    holder.leftChatProfileCircle.setImageResource(CommonConst.SSOM.equals(roomItem.getSsomType()) ? R.drawable.chat_profile_border_green : R.drawable.chat_profile_border_red);
                } else {
                    holder.leftChatProfileLayout.setVisibility(View.INVISIBLE);
                }

                holder.receiveMessage.setBackgroundResource(CommonConst.SSOM.equals(roomItem.getSsomType()) ? R.drawable.bg_receive_message_green : R.drawable.bg_receive_message_red);
                holder.receiveMessage.setText(item.getMsg());
                if(getItem(position + 1) != null && item.getFromUserId().equals(getItem(position + 1).getFromUserId())
                        && Util.isSameTimeBetweenTwoTimes(item.getTimestamp(), getItem(position + 1).getTimestamp())) {
                    holder.receiveTime.setVisibility(View.GONE);
                } else {
                    holder.receiveTime.setVisibility(View.VISIBLE);
                    holder.receiveTime.setText(Util.getTimeTextForMessage(item.getTimestamp()));
                }
            } else {
                /**
                 * send layout setting
                 */
                holder.receiveMessageLayout.setVisibility(View.GONE);
                holder.sendMessageLayout.setVisibility(View.VISIBLE);

                if(position == 0 || getItem(position - 1) == null || !item.getFromUserId().equals(getItem(position - 1).getFromUserId())
                        || !Util.isSameTimeBetweenTwoTimes(item.getTimestamp(), getItem(position - 1).getTimestamp())) {
                    holder.rightChatProfileLayout.setVisibility(View.VISIBLE);
                    // profile image
                    holder.rightChatProfileImage.setDefaultImageResId(R.drawable.profile_img_basic);
                    holder.rightChatProfileImage.setErrorImageResId(R.drawable.profile_img_basic);
                    holder.rightChatProfileImage.setImageUrl(((BaseActivity) context).getUserId().equals(roomItem.getOwnerId()) ?
                            roomItem.getOwnerImageUrl() : roomItem.getParticipantImageUrl(), mImageLoader);
                } else {
                    holder.rightChatProfileLayout.setVisibility(View.INVISIBLE);
                }

                holder.sendMessage.setText(item.getMsg());
                if(getItem(position + 1) != null && item.getFromUserId().equals(getItem(position + 1).getFromUserId())
                        && Util.isSameTimeBetweenTwoTimes(item.getTimestamp(), getItem(position + 1).getTimestamp())) {
                    holder.sendTime.setVisibility(View.GONE);
                } else {
                    holder.sendTime.setVisibility(View.VISIBLE);
                    holder.sendTime.setText(Util.getTimeTextForMessage(item.getTimestamp()));
                }
            }
        }

        return convertView;
    }

    public void add(ChattingItem item) {
        if(CommonConst.Chatting.SYSTEM.equals(item.getMsgType())) {
            switch (item.getMsg()) {
                case CommonConst.Chatting.MEETING_REQUEST :
                    item.setStatus(ChattingItem.MessageType.request);
                    break;
                case CommonConst.Chatting.MEETING_APPROVE :
                    item.setStatus(ChattingItem.MessageType.approve);
                    break;
                case CommonConst.Chatting.MEETING_CANCEL :
                    item.setStatus(ChattingItem.MessageType.cancel);
                    break;
                case CommonConst.Chatting.MEETING_COMPLETE :
                    item.setStatus(ChattingItem.MessageType.finish);
                    break;
            }
        }
        itemList.add(item);
    }

    public void add(String message) {
        ChattingItem item = new ChattingItem();
        item.setMsg(message);
        item.setFromUserId(((BaseActivity) context).getUserId());
        item.setTimestamp(System.currentTimeMillis());
        item.setMsgType(CommonConst.Chatting.NORMAL);
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
        private FrameLayout leftChatProfileLayout;
        private CircularNetworkImageView leftChatProfileImage;
        private ImageView leftChatProfileCircle;
        private TextView receiveMessage;
        private TextView receiveTime;

        /**
         * Send message layout
         */
        private RelativeLayout sendMessageLayout;
        private FrameLayout rightChatProfileLayout;
        private CircularNetworkImageView rightChatProfileImage;
        private ImageView rightChatProfileCircle;
        private TextView sendMessage;
        private TextView sendTime;

        /**
         * Initial ssom message
         */
        private LinearLayout initialMessage;

        /**
         * Request ssom message
         */
        private TextView requestMessage;

        /**
         * Approve ssom message
         */
        private TextView approveMessage;

        /**
         * Finish ssom message
         */
        private TextView finishMessage;
    }
}
