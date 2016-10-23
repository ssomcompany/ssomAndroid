package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.widget.CircularNetworkImageView;

import java.util.ArrayList;

public class ChatRoomListAdapter extends BaseAdapter {
    private static final String TAG = ChatRoomListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private Context context;
    private ImageLoader mImageLoader;
    private ArrayList<ChatRoomItem> itemList;

    public ChatRoomListAdapter(Context context){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = NetworkManager.getInstance().getImageLoader();
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
            convertView = mInflater.inflate(R.layout.ssom_chat_list_item, null);

            holder = new ChatItemViewHolder();
            holder.setImage((CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image));
            holder.setIconCircle((ImageView) convertView.findViewById(R.id.icon_circle));
            holder.setTvChatInfo((TextView) convertView.findViewById(R.id.chat_information));
            holder.setTvChatContent((TextView) convertView.findViewById(R.id.chat_content));
            holder.setUnreadLayout((FrameLayout) convertView.findViewById(R.id.unread_layout));
            holder.setImgUnreadCount((ImageView) convertView.findViewById(R.id.bg_unread_count));
            holder.setUnreadCount((TextView) convertView.findViewById(R.id.unread_count));
            holder.setTvDistance((TextView) convertView.findViewById(R.id.tv_distance));
            holder.setTvTime((TextView) convertView.findViewById(R.id.tv_time));

            convertView.setTag(holder);
        } else {
            holder = (ChatItemViewHolder) convertView.getTag();
        }

        // list view item setting
        ChatRoomItem item = itemList.get(position);

        // profile image
        holder.getImage().setDefaultImageResId(R.drawable.profile_img_basic);
        holder.getImage().setErrorImageResId(R.drawable.profile_img_basic);
        holder.getImage().setImageUrl(((BaseActivity) context).getUserId().equals(item.getOwnerId()) ?
                item.getParticipantImageUrl() : item.getOwnerImageUrl(), mImageLoader);

        //icon
        if(CommonConst.SSOM.equals(item.getSsomType())){
            holder.getIconCircle().setImageResource(R.drawable.chat_profile_border_green);
        }else{
            holder.getIconCircle().setImageResource(R.drawable.chat_profile_border_red);
        }

        // chat info
        if(item.getMinAge() == 0 || item.getUserCount() == 0) {
            holder.getTvChatInfo().setText(context.getString(R.string.chat_room_no_info));
        } else {
            holder.getTvChatInfo().setText(String.format(context.getResources().getString(R.string.post_title),
                    Util.convertAgeRangeAtBackOneChar(item.getMinAge()), item.getUserCount()));
        }

        // content
        if(CommonConst.Chatting.MEETING_REQUEST.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_REQUEST.equals(item.getLastMsg())) {
            String sysStr = context.getString(((BaseActivity) context).getUserId().equals(item.getRequestId()) ?
                    R.string.chat_message_request_sent : R.string.chat_message_request_received);
            holder.getTvChatContent().setText(getSystemMsg(sysStr), TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_APPROVE.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_APPROVE.equals(item.getLastMsg())) {
            String sysStr = context.getString(R.string.chat_message_approve);
            holder.getTvChatContent().setText(getSystemMsg(sysStr), TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_CANCEL.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_CANCEL.equals(item.getLastMsg())) {
            String sysStr = context.getString(R.string.chat_message_finish);
            holder.getTvChatContent().setText(getSystemMsg(sysStr), TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_COMPLETE.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_COMPLETE.equals(item.getLastMsg())) {
            String sysStr = context.getString(R.string.chat_message_finish);
            holder.getTvChatContent().setText(getSystemMsg(sysStr), TextView.BufferType.SPANNABLE);
        } else {
            holder.getTvChatContent().setText(TextUtils.isEmpty(item.getLastMsg()) ? getEmptyLastMsg() : item.getLastMsg(),
                    TextUtils.isEmpty(item.getLastMsg()) ? TextView.BufferType.SPANNABLE : TextView.BufferType.NORMAL);
        }

        if(item.getUnreadCount() > 0) {
            holder.getUnreadLayout().setVisibility(View.VISIBLE);
            // TODO 쏨 요청이 왔을 때 하트로 변경 icon_heart_ssom_ing
            holder.getImgUnreadCount().setImageResource(R.drawable.icon_chat_unread_count);
            // TODO 쏨 요청이 왔을 때 count view 숨김
            holder.getUnreadCount().setText(String.valueOf(item.getUnreadCount()));
        } else {
            // TODO 쏨 요청이 왔을 경우에는 하트로 변경해야 함
            holder.getUnreadLayout().setVisibility(View.INVISIBLE);
        }

        // distance
        holder.getTvDistance().setText(LocationTracker.getInstance().getDistanceString(item.getLatitude(), item.getLongitude()));

        //time
        holder.getTvTime().setText(Util.getTimeTextForChatRoom(item.getLastTimestamp()));

        return convertView;
    }

    private SpannableStringBuilder getSystemMsg(String sysStr) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString redSpannable= new SpannableString(sysStr);
        redSpannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red_pink)), 0, sysStr.length(), 0);
        builder.append(redSpannable);
        return builder;
    }

    private SpannableStringBuilder getEmptyLastMsg() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String firstStr = context.getString(R.string.chat_message_initial_first);
        SpannableString redSpannable= new SpannableString(firstStr);
        redSpannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red_pink)), 0, firstStr.length(), 0);
        builder.append(redSpannable);
        String secondStr = context.getString(R.string.chat_message_initial_second);
        SpannableString whiteSpannable= new SpannableString(secondStr);
        whiteSpannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.pinkish_gray_two)), 0, secondStr.length(), 0);
        builder.append(whiteSpannable);

        return builder;
    }

    private class ChatItemViewHolder {
        /**
         * holder for list items
         */
        private CircularNetworkImageView image;
        private ImageView iconCircle;
        private TextView tvChatInfo;
        private TextView tvChatContent;
        private FrameLayout unreadLayout;
        private ImageView imgUnreadCount;
        private TextView unreadCount;
        private TextView tvDistance;
        private TextView tvTime;

        public CircularNetworkImageView getImage() {
            return image;
        }

        public void setImage(CircularNetworkImageView image) {
            this.image = image;
        }

        public ImageView getIconCircle() {
            return iconCircle;
        }

        public void setIconCircle(ImageView iconCircle) {
            this.iconCircle = iconCircle;
        }

        public TextView getTvChatInfo() {
            return tvChatInfo;
        }

        public void setTvChatInfo(TextView tvChatInfo) {
            this.tvChatInfo = tvChatInfo;
        }

        public TextView getTvChatContent() {
            return tvChatContent;
        }

        public void setTvChatContent(TextView tvChatContent) {
            this.tvChatContent = tvChatContent;
        }

        public FrameLayout getUnreadLayout() {
            return unreadLayout;
        }

        public void setUnreadLayout(FrameLayout unreadLayout) {
            this.unreadLayout = unreadLayout;
        }

        public ImageView getImgUnreadCount() {
            return imgUnreadCount;
        }

        public void setImgUnreadCount(ImageView imgUnreadCount) {
            this.imgUnreadCount = imgUnreadCount;
        }

        public TextView getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(TextView unreadCount) {
            this.unreadCount = unreadCount;
        }

        public TextView getTvDistance() {
            return tvDistance;
        }

        public void setTvDistance(TextView tvDistance) {
            this.tvDistance = tvDistance;
        }

        public TextView getTvTime() {
            return tvTime;
        }

        public void setTvTime(TextView tvTime) {
            this.tvTime = tvTime;
        }
    }
}
