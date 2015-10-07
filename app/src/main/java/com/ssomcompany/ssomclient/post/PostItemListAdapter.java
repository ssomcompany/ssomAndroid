package com.ssomcompany.ssomclient.post;

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
import com.ssomcompany.ssomclient.common.CategoryUtil;
import com.ssomcompany.ssomclient.common.CircularNetworkImageView;
import com.ssomcompany.ssomclient.common.VolleyUtil;

/**
 * Created by kshgizmo on 2015. 9. 11..
 */
public class PostItemListAdapter extends BaseAdapter{

    private Context context;
    public PostItemListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return PostContent.ITEMS.size();
    }

    @Override
    public Object getItem(int position) {
        if(getCount() > position) {
            return PostContent.ITEMS.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return PostContent.ITEMS.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.ssom_list_item, null);
        }
        TextView txtTitle = (TextView) convertView.findViewById(R.id.content);
        PostContent.PostItem row_pos = PostContent.ITEMS.get(position);
        // setting the image resource and title
        txtTitle.setText(row_pos.toString());
        //TODO replace ImageRequest to ImageLoader for performance

        final CircularNetworkImageView image = (CircularNetworkImageView) convertView.findViewById(R.id.icon_list_image);
        ImageLoader mImageLoader = VolleyUtil.getInstance(getContext()).getImageLoader();
        image.setImageUrl(row_pos.getImage(), mImageLoader);
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
        ImageView iconView = (ImageView) convertView.findViewById(R.id.icon_list_r);
        if("ssom".equals(row_pos.ssom)){
            iconView.setImageResource(R.drawable.icon_list_st_b);
        }else{
            iconView.setImageResource(R.drawable.icon_list_st_r);
        }


        //age
        TextView ageTextView = (TextView) convertView.findViewById(R.id.list_text_age);
        ageTextView.setText(row_pos.minAge+"~"+row_pos.maxAge);
        //user count
        TextView userCountTextView = (TextView) convertView.findViewById(R.id.list_text_user_count);
        userCountTextView.setText(""+row_pos.userCount);
        //distance
        TextView distanceTextView = (TextView) convertView.findViewById(R.id.list_text_distance);
        distanceTextView.setText("300m"); //TODO change to real data
        //category
        int iconId = CategoryUtil.getCategoryIconId(row_pos.category);
        if(iconId!=-1){
            ImageView iconCategoty = (ImageView) convertView.findViewById(R.id.icon_category);
            iconCategoty.setImageResource(iconId);
        }


        return convertView;
    }
    public Context getContext() {
        return context;
    }
}
