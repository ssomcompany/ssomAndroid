package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.widget.CircularNetworkImageView;

import java.util.ArrayList;

public class SsomItemListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private ImageLoader mImageLoader;

    private ArrayList<SsomItem> itemList;

    public SsomItemListAdapter(Context context){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = NetworkManager.getInstance().getImageLoader();
    }

    public void setItemList(ArrayList<SsomItem> itemList) {
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
        PostItemViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ssom_list_item, null);

            holder = new PostItemViewHolder();

            holder.titleTv = (TextView) convertView.findViewById(R.id.post_item_title);
            holder.timeTv = (TextView) convertView.findViewById(R.id.post_item_time);
            holder.distanceTv = (TextView) convertView.findViewById(R.id.post_item_distance);
            holder.contentTv = (TextView) convertView.findViewById(R.id.content);
            holder.image = (CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image);
            holder.iconView = (ImageView) convertView.findViewById(R.id.icon_list_r);

            convertView.setTag(holder);
        } else {
            holder = (PostItemViewHolder) convertView.getTag();
        }

        // list view item setting
        SsomItem item = itemList.get(position);

        // profile
        holder.image.setDefaultImageResId(R.drawable.profile_img_basic);
        holder.image.setErrorImageResId(R.drawable.profile_img_basic);
        holder.image.setImageUrl(item.getImageUrl(), mImageLoader);

        //icon
        if(CommonConst.SSOM.equals(item.getSsomType())){
            holder.iconView.setImageResource(R.drawable.icon_list_st_g);
        }else{
            holder.iconView.setImageResource(R.drawable.icon_list_st_r);
        }

        // title
        holder.titleTv.setText(String.format(context.getResources().getString(R.string.post_title),
                Util.convertAgeRangeAtBackOneChar(item.getMinAge()), item.getUserCount()));

        //time
        holder.timeTv.setText(Util.getTimeText(item.getCreatedTimestamp() == 0 ? 0 : item.getCreatedTimestamp()));

        // distance
        holder.distanceTv.setText(LocationTracker.getInstance().getDistanceString(item.getLatitude(), item.getLongitude()));

        // content
        holder.contentTv.setText(item.getContent());

        return convertView;
    }

    private class PostItemViewHolder {
        /**
         * holder for list items
         */
        public ImageView iconView;
        public CircularNetworkImageView image;
        public TextView titleTv;
        public TextView timeTv;
        public TextView distanceTv;
        public TextView contentTv;
    }
}
