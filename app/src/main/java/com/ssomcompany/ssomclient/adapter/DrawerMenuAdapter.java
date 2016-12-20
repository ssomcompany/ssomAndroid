package com.ssomcompany.ssomclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;

public class DrawerMenuAdapter extends BaseAdapter {
    private int[] menuList = {R.string.private_information, R.string.use_policy};

    // context instance
    private LayoutInflater mInflater;
    private Context mContext;

    public DrawerMenuAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getItem(int position) {
        return menuList[position];
    }

    @Override
    public int getCount() {
        return menuList.length;
    }

    @Override
    public long getItemId(int position) {
        return menuList[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.view_drawer_menu, null);

            holder = new MenuHolder();
            holder.tvDrawerMenu = (TextView) convertView.findViewById(R.id.tv_drawer_menu);
//            holder.imgDrawerMenu = (ImageView) convertView.findViewById(R.id.img_drawer_menu);
            convertView.setTag(holder);
        } else {
            holder = (MenuHolder) convertView.getTag();
        }

        switch (position) {
            case 0:
//                holder.imgDrawerMenu.setBackgroundResource(R.drawable.icon_lock);
                holder.tvDrawerMenu.setText(mContext.getString(R.string.private_information));
                break;
            case 1:
//                holder.imgDrawerMenu.setBackgroundResource(R.drawable.icon_book);
                holder.tvDrawerMenu.setText(mContext.getString(R.string.use_policy));
                break;
            case 2:
//                holder.imgDrawerMenu.setBackgroundResource(R.drawable.icon_arlet);
                holder.tvDrawerMenu.setText(mContext.getString(R.string.withdraw));
                break;
        }

        return convertView;
    }

    private class MenuHolder {
        // view setting
        TextView tvDrawerMenu;
//        ImageView imgDrawerMenu;
    }
}
