package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.model.ChatRoomItem;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChatRoomListAdapter extends BaseAdapter {
    private static final String TAG = ChatRoomListAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<ChatRoomItem> itemList;

    public ChatRoomListAdapter(Context context){
        this.context = context;
    }

    public void setChatRoomList(ArrayList<ChatRoomItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList == null? 0 : itemList.size();
    }

    @Override
    public Object getItem(int position) {
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
        ChatItemViewHolder holder;

        if (convertView == null) {
            convertView = ((LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ssom_chat_list_item, null);

            holder = new ChatItemViewHolder();
            holder.itemLayout = convertView.findViewById(R.id.item_layout);
            holder.image = (ImageView) convertView.findViewById(R.id.icon_list_image);
            holder.iconCircle = (ImageView) convertView.findViewById(R.id.icon_circle);
            holder.iconIng = (ImageView) convertView.findViewById(R.id.icon_ing);
            holder.tvChatInfo = (TextView) convertView.findViewById(R.id.chat_information);
            holder.tvChatContent = (TextView) convertView.findViewById(R.id.chat_content);
            holder.unreadLayout = (FrameLayout) convertView.findViewById(R.id.unread_layout);
            holder.imgUnreadCount = (ImageView) convertView.findViewById(R.id.bg_unread_count);
            holder.unreadCount = (TextView) convertView.findViewById(R.id.unread_count);
            holder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (ChatItemViewHolder) convertView.getTag();
        }

        // list view item setting
        ChatRoomItem item = itemList.get(position);

        if(CommonConst.Chatting.MEETING_REQUEST.equals(item.getStatus())) {
            holder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.pink_10));
        } else {
            holder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        // profile image
        Glide.with(context).load(((BaseActivity) context).getUserId().equals(item.getOwnerId()) ?
                item.getParticipantThumbnailImageUrl() : item.getOwnerThumbnailImageUrl())
                .crossFade()
                .placeholder(R.drawable.profile_img_basic)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.image);

        //icon
        if(CommonConst.SSOM.equals(item.getSsomType())){
            holder.iconCircle.setImageResource(R.drawable.chat_profile_border_green);
        }else{
            holder.iconCircle.setImageResource(R.drawable.chat_profile_border_red);
        }

        // ing image
        if(CommonConst.Chatting.MEETING_APPROVE.equals(item.getStatus())) {
            holder.iconIng.setVisibility(View.VISIBLE);
            holder.iconIng.setImageResource(CommonConst.SSOM.equals(item.getSsomType()) ?
                    R.drawable.chat_ssom_ing_green : R.drawable.chat_ssom_ing_red);
        } else {
            holder.iconIng.setVisibility(View.GONE);
        }

        // chat info
        if(item.getMinAge() == 0 || item.getUserCount() == 0) {
            holder.tvChatInfo.setText(context.getString(R.string.chat_room_no_info));
        } else {
            holder.tvChatInfo.setText(String.format(context.getResources().getString(R.string.post_title),
                    Util.convertAgeRangeAtBackOneChar(item.getMinAge()), item.getUserCount()));
        }

        // content
        SpannableStringBuilder sysStr;
        if(CommonConst.Chatting.MEETING_REQUEST.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_REQUEST.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, ((BaseActivity) context).getUserId().equals(item.getRequestId()) ?
                    R.string.chat_message_request_sent : R.string.chat_message_request_received, R.color.red_pink);
            holder.tvChatContent.setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_APPROVE.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_APPROVE.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_approve, R.color.red_pink);
            holder.tvChatContent.setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_CANCEL.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_cancel_first, R.color.red_pink);
            holder.tvChatContent.setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_COMPLETE.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_complete, R.color.red_pink);
            holder.tvChatContent.setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_OUT.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_finish, R.color.red_pink);
            holder.tvChatContent.setText(sysStr, TextView.BufferType.SPANNABLE);
        } else {
            holder.tvChatContent.setText(TextUtils.isEmpty(item.getLastMsg()) ? getEmptyLastMsg() : item.getLastMsg(),
                    TextUtils.isEmpty(item.getLastMsg()) ? TextView.BufferType.SPANNABLE : TextView.BufferType.NORMAL);
        }

        if(item.getUnreadCount() > 0) {
            holder.unreadLayout.setVisibility(View.VISIBLE);
            // TODO 쏨 요청이 왔을 때 하트로 변경 icon_heart_ssom_ing
            holder.imgUnreadCount.setImageResource(R.drawable.icon_chat_unread_count);
            // TODO 쏨 요청이 왔을 때 count view 숨김
            holder.unreadCount.setText(String.valueOf(item.getUnreadCount()));
        } else {
            // TODO 쏨 요청이 왔을 경우에는 하트로 변경해야 함
            holder.unreadLayout.setVisibility(View.INVISIBLE);
        }

        // distance
        holder.tvDistance.setText(LocationTracker.getInstance().getDistanceString(item.getLatitude(), item.getLongitude()));

        //time
        holder.tvTime.setText(Util.getTimeTextForChatRoom(item.getLastTimestamp()));

        return convertView;
    }

    private SpannableStringBuilder getEmptyLastMsg() {
        return Util.getSystemMsg(context, R.string.chat_message_initial_first, R.color.red_pink)
                .append(Util.getSystemMsg(context, R.string.chat_message_initial_second, R.color.pinkish_gray_two));
    }

    private class ChatItemViewHolder {
        /**
         * holder for list items
         */
        View itemLayout;
        public ImageView image;
        ImageView iconCircle;
        ImageView iconIng;
        TextView tvChatInfo;
        TextView tvChatContent;
        FrameLayout unreadLayout;
        ImageView imgUnreadCount;
        TextView unreadCount;
        TextView tvDistance;
        TextView tvTime;
    }
}
