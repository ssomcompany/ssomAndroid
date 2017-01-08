package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.BitmapWorkerTask;
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
        final PostItemViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ssom_list_item, null);

            holder = new PostItemViewHolder();

            holder.titleTv = (TextView) convertView.findViewById(R.id.post_item_title);
            holder.timeTv = (TextView) convertView.findViewById(R.id.post_item_time);
            holder.distanceTv = (TextView) convertView.findViewById(R.id.post_item_distance);
            holder.contentTv = (TextView) convertView.findViewById(R.id.content);
            holder.image = (CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image);
            holder.iconView = (ImageView) convertView.findViewById(R.id.icon_list_r);
            holder.markView = (ImageView) convertView.findViewById(R.id.icon_list_mark);
            holder.iconIng = (ImageView) convertView.findViewById(R.id.icon_ing);

            convertView.setTag(holder);
        } else {
            holder = (PostItemViewHolder) convertView.getTag();
        }

        // list view item setting
        final SsomItem item = itemList.get(position);

        // profile
        holder.image.setDefaultImageResId(R.drawable.profile_img_basic);
        holder.image.setErrorImageResId(R.drawable.profile_img_basic);
        if(NetworkManager.getInstance().hasBitmapInCache(item.getThumbnailImageUrl())) {
            if(NetworkManager.getInstance().hasBitmapFromMemoryCache(item.getThumbnailImageUrl())) {
                // get bitmap from memory cache
                holder.image.setLocalImageBitmap(NetworkManager.getInstance().getBitmapFromMemoryCache(item.getThumbnailImageUrl()));
            } else {
                // get bitmap from disk cache
                BitmapWorkerTask diskCacheTask = new BitmapWorkerTask() {
                    @Override
                    protected void onPostExecute(Bitmap result) {
                        super.onPostExecute(result);
                        if (result != null) {
                            // Add final bitmap to caches
                            NetworkManager.getInstance().addBitmapToCache(item.getThumbnailImageUrl(), result);
                            holder.image.setLocalImageBitmap(result);
                        }
                    }
                };

                diskCacheTask.execute(item.getThumbnailImageUrl());
            }
        } else {
            holder.image.setImageUrl(item.getThumbnailImageUrl(), mImageLoader);
        }

        //icon
        if(CommonConst.SSOM.equals(item.getSsomType())){
            holder.iconView.setImageResource(R.drawable.list_green_border);
            holder.markView.setImageResource(R.drawable.list_mark_green);
        }else{
            holder.iconView.setImageResource(R.drawable.list_red_border);
            holder.markView.setImageResource(R.drawable.list_mark_red);
        }

        // ing image
        if(CommonConst.Chatting.MEETING_APPROVE.equals(item.getStatus())) {
            holder.iconIng.setVisibility(View.VISIBLE);
            holder.iconIng.setImageResource(CommonConst.SSOM.equals(item.getSsomType()) ?
                    R.drawable.ssom_ing_green_big : R.drawable.ssom_ing_red_big);
        } else {
            holder.iconIng.setVisibility(View.GONE);
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
        public ImageView markView;
        public ImageView iconIng;
        public CircularNetworkImageView image;
        public TextView titleTv;
        public TextView timeTv;
        public TextView distanceTv;
        public TextView contentTv;
    }
}
