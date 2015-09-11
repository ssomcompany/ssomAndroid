package com.ssomcompany.ssomclient.post;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;

/**
 * Created by cellz on 2015. 9. 11..
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
        return PostContent.ITEMS.get(position);
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

        return convertView;
    }
}
