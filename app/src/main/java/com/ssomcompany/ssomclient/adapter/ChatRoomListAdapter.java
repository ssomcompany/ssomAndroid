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

import com.android.volley.toolbox.ImageLoader;
import com.ssomcompany.ssomclient.R;
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
    private ChatItemViewHolder holder;
    private ArrayList<ChatRoomItem> itemList;

    public ChatRoomListAdapter(Context context, ArrayList<ChatRoomItem> itemList){
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ssom_chat_list_item, null);

            holder = new ChatItemViewHolder();
            holder.image = (CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image);
            holder.iconCircle = (ImageView) convertView.findViewById(R.id.icon_circle);
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

        // profile image
        holder.image.setDefaultImageResId(R.drawable.profile_img_basic);
        holder.image.setErrorImageResId(R.drawable.profile_img_basic);
        holder.image.setImageUrl(item.getImageUrl(), mImageLoader);

        //icon
        if(CommonConst.SSOM.equals(item.getSsomType())){
            holder.iconCircle.setImageResource(R.drawable.chat_profile_border_green);
        }else{
            holder.iconCircle.setImageResource(R.drawable.chat_profile_border_red);
        }

        // chat info
        holder.tvChatInfo.setText(String.format(context.getResources().getString(R.string.post_title),
                Util.convertAgeRangeAtBackOneChar(item.getMinAge()), item.getUserCount()));

        // content
        holder.tvChatContent.setText(TextUtils.isEmpty(item.getLastMsg()) ? getEmptyLastMsg() : item.getLastMsg(),
                TextUtils.isEmpty(item.getLastMsg()) ? TextView.BufferType.SPANNABLE : TextView.BufferType.NORMAL);

        if(item.getUnreadCount() > 0) {
            holder.unreadLayout.setVisibility(View.VISIBLE);
            // TODO 쏨 요청이 왔을 때 하트로 변경 icon_heart_ssom_ing
            holder.imgUnreadCount.setImageResource(R.drawable.icon_chat_unread_count);
            // TODO 쏨 요청이 왔을 때 count view 숨김
            holder.unreadCount.setText(item.getUnreadCount());
        } else {
            // TODO 쏨 요청이 왔을 경우에는 하트로 변경해야 함
            holder.unreadLayout.setVisibility(View.INVISIBLE);
        }

        // distance
        holder.tvDistance.setText(LocationTracker.getInstance().getDistanceString(item.getLatitude(), item.getLongitude()));

        //time
//        holder.tvTime.setText(String.valueOf(item.getMessageTime()));
        holder.tvTime.setText(Util.getTimeTextForChatRoom(item.getLastTimestamp()));

        return convertView;
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
        public CircularNetworkImageView image;
        public ImageView iconCircle;
        public TextView tvChatInfo;
        public TextView tvChatContent;
        public FrameLayout unreadLayout;
        public ImageView imgUnreadCount;
        public TextView unreadCount;
        public TextView tvDistance;
        public TextView tvTime;
    }
}
