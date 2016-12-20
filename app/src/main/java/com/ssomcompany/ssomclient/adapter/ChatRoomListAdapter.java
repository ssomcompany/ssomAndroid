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
            convertView = mInflater.inflate(R.layout.ssom_chat_list_item, parent, false);

            holder = new ChatItemViewHolder();
            holder.setItemLayout(convertView.findViewById(R.id.item_layout));
            holder.setImage((CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image));
            holder.setIconCircle((ImageView) convertView.findViewById(R.id.icon_circle));
            holder.setIconIng((ImageView) convertView.findViewById(R.id.icon_ing));
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

        if(CommonConst.Chatting.MEETING_REQUEST.equals(item.getStatus())) {
            holder.getItemLayout().setBackgroundColor(context.getResources().getColor(R.color.pink_10));
        } else {
            holder.getItemLayout().setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        // profile image
        holder.getImage().setDefaultImageResId(R.drawable.profile_img_basic);
        holder.getImage().setErrorImageResId(R.drawable.profile_img_basic);
        holder.getImage().setImageUrl(((BaseActivity) context).getUserId().equals(item.getOwnerId()) ?
                item.getParticipantImageUrl() + "?thumbnail=200" : item.getOwnerImageUrl() + "?thumbnail=200", mImageLoader);

        //icon
        if(CommonConst.SSOM.equals(item.getSsomType())){
            holder.getIconCircle().setImageResource(R.drawable.chat_profile_border_green);
        }else{
            holder.getIconCircle().setImageResource(R.drawable.chat_profile_border_red);
        }

        // ing image
        if(CommonConst.Chatting.MEETING_APPROVE.equals(item.getStatus())) {
            holder.getIconIng().setVisibility(View.VISIBLE);
            holder.getIconIng().setImageResource(CommonConst.SSOM.equals(item.getSsomType()) ?
                    R.drawable.chat_ssom_ing_green : R.drawable.chat_ssom_ing_red);
        } else {
            holder.getIconIng().setVisibility(View.GONE);
        }

        // chat info
        if(item.getMinAge() == 0 || item.getUserCount() == 0) {
            holder.getTvChatInfo().setText(context.getString(R.string.chat_room_no_info));
        } else {
            holder.getTvChatInfo().setText(String.format(context.getResources().getString(R.string.post_title),
                    Util.convertAgeRangeAtBackOneChar(item.getMinAge()), item.getUserCount()));
        }

        // content
        SpannableStringBuilder sysStr;
        if(CommonConst.Chatting.MEETING_REQUEST.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_REQUEST.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, ((BaseActivity) context).getUserId().equals(item.getRequestId()) ?
                    R.string.chat_message_request_sent : R.string.chat_message_request_received, R.color.red_pink);
            holder.getTvChatContent().setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_APPROVE.equals(item.getStatus()) &&
                CommonConst.Chatting.MEETING_APPROVE.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_approve, R.color.red_pink);
            holder.getTvChatContent().setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_CANCEL.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_cancel_first, R.color.red_pink);
            holder.getTvChatContent().setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_COMPLETE.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_complete, R.color.red_pink);
            holder.getTvChatContent().setText(sysStr, TextView.BufferType.SPANNABLE);
        } else if(CommonConst.Chatting.MEETING_OUT.equals(item.getLastMsg())) {
            sysStr = Util.getSystemMsg(context, R.string.chat_message_finish, R.color.red_pink);
            holder.getTvChatContent().setText(sysStr, TextView.BufferType.SPANNABLE);
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

    private SpannableStringBuilder getEmptyLastMsg() {
        return Util.getSystemMsg(context, R.string.chat_message_initial_first, R.color.red_pink)
                .append(Util.getSystemMsg(context, R.string.chat_message_initial_second, R.color.pinkish_gray_two));
    }

    private class ChatItemViewHolder {
        /**
         * holder for list items
         */
        private View itemLayout;
        private CircularNetworkImageView image;
        private ImageView iconCircle;
        private ImageView iconIng;
        private TextView tvChatInfo;
        private TextView tvChatContent;
        private FrameLayout unreadLayout;
        private ImageView imgUnreadCount;
        private TextView unreadCount;
        private TextView tvDistance;
        private TextView tvTime;

        public View getItemLayout() {
            return itemLayout;
        }

        public void setItemLayout(View itemLayout) {
            this.itemLayout = itemLayout;
        }

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

        public ImageView getIconIng() {
            return iconIng;
        }

        public void setIconIng(ImageView iconIng) {
            this.iconIng = iconIng;
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
