package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
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
        PostItemViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ssom_list_item, null);

            holder = new PostItemViewHolder();

            holder.titleTv = (TextView) convertView.findViewById(R.id.post_item_title);
            holder.timeTv = (TextView) convertView.findViewById(R.id.post_item_time);
            holder.distanceTv = (TextView) convertView.findViewById(R.id.post_item_distance);
            holder.contentTv = (TextView) convertView.findViewById(R.id.content);
            holder.image = (CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image);

            convertView.setTag(holder);
        } else {
            holder = (PostItemViewHolder) convertView.getTag();
        }

        // list view item setting
        SsomItem item = itemList.get(position);

        holder.image.setImageUrl(item.getImageUrl(), mImageLoader);
//        mImageLoader.get(row_pos.getImage(), ImageLoader.getImageListener(image,R.drawable.icon_wirte_photo_emp, R.drawable.icon_wirte_photo_emp));
//        ImageRequest imageRequest = new ImageRequest(row_pos.getImage(), new Response.Listener<Bitmap>() {
//            @Override
//            public void onResponse(Bitmap bitmap) {
//                image.setImageDrawable(Util.getCircleBitmap(bitmap, 266));
//            }
//        },144, 256, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888
//                , new Response.ErrorListener(){
//
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        });
//        VolleyUtil.getInstance(getContext()).getRequestQueue().add(imageRequest);

        //icon
        holder.iconView = (ImageView) convertView.findViewById(R.id.icon_list_r);
        if(CommonConst.SSOM.equals(item.getSsom())){
            holder.iconView.setImageResource(R.drawable.icon_list_st_g);
        }else{
            holder.iconView.setImageResource(R.drawable.icon_list_st_r);
        }

        // title
        holder.titleTv.setText(String.format(context.getResources().getString(R.string.post_title),
                "20대 초", item.getUserCount()));

        //time
        holder.timeTv.setText(Util.getTimeText(Long.valueOf(item.getPostId())));

        // distance
        holder.distanceTv.setText(LocationTracker.getInstance().getDistanceString(item.getLatitude(), item.getLongitude()));

        // content
        holder.contentTv.setText(item.getContent());

//        if(getCount() - 1 == position) mSsomAdapterListener.onNotifyFinished();
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
