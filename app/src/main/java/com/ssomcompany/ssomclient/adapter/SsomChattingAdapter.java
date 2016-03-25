package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
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
import com.ssomcompany.ssomclient.common.LocationUtil;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.widget.CircularNetworkImageView;

import java.util.ArrayList;

public class SsomChattingAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private ImageLoader mImageLoader;

    private ArrayList<ChattingItem> itemList;

    public SsomChattingAdapter(Context context){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = NetworkManager.getInstance().getImageLoader();
    }

    public void setItemList(ArrayList<ChattingItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
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
            holder.deleteLayout = (FrameLayout) convertView.findViewById(R.id.delete_layout);
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
        ChattingItem item = itemList.get(position);

        // delete button
        holder.deleteLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO delete chatting room
            }
        });

        // profile image
        holder.image.setImageUrl(item.getImageUrl(), mImageLoader);

        //icon
        if(CommonConst.SSOM.equals(item.getSsom())){
            holder.iconCircle.setImageResource(R.drawable.chat_profile_border_green);
        }else{
            holder.iconCircle.setImageResource(R.drawable.chat_profile_border_red);
        }

        // chat info
        holder.tvChatInfo.setText(String.format(context.getResources().getString(R.string.post_title),
                "20대 초", item.getUserCount()));

        // content
        holder.tvChatContent.setText(item.getMessage());

        // TODO count 가 없을 경우 view 숨김
        holder.unreadLayout.setVisibility(View.VISIBLE);
        // TODO 쏨 요청이 왔을 때 하트로 변경 icon_heart_ssom_ing
        holder.imgUnreadCount.setImageResource(R.drawable.icon_chat_unread_count);
        // TODO 쏨 요청이 왔을 때 count view 숨김
        holder.unreadCount.setText("3");

        // distance
        holder.tvDistance.setText(LocationUtil.getDistanceString(item));

        //time
//        holder.tvTime.setText(String.valueOf(item.getMessageTime()));
        holder.tvTime.setText("오후 7:44");

//        if(getCount() - 1 == position) mSsomAdapterListener.onNotifyFinished();
        return convertView;
    }

    private class ChatItemViewHolder {
        /**
         * holder for list items
         */
        public FrameLayout deleteLayout;
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
