package com.ssomcompany.ssomclient.post;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CircularNetworkImageView;
import com.ssomcompany.ssomclient.common.LocationUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;

import java.util.ArrayList;

/**
 * Created by kshgizmo on 2015. 9. 11..
 */
public class PostItemListAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Context context;
    private ImageLoader mImageLoader;

    private ArrayList<PostContent.PostItem> itemList;

    public PostItemListAdapter(Context context){
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        this.mImageLoader = VolleyUtil.getInstance(context).getImageLoader();
    }

    public void setItemList(ArrayList<PostContent.PostItem> itemList) {
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
        PostContent.PostItem item = itemList.get(position);

        holder.image.setImageUrl(item.getImage(), mImageLoader);
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
        if("ssom".equals(item.ssom)){
            holder.iconView.setImageResource(R.drawable.icon_list_st_g);
        }else{
            holder.iconView.setImageResource(R.drawable.icon_list_st_r);
        }

        // title
        holder.titleTv.setText(String.format(context.getResources().getString(R.string.post_title),
                "20대 초", item.userCount));

        //time
        holder.timeTv.setText(Util.getTimeText(Long.valueOf(item.postId)));

        // distance
        holder.distanceTv.setText(LocationUtil.getDistanceString(item));

        // content
        holder.contentTv.setText(item.content);

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
