package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.ssomcompany.ssomclient.common.RoundImage;
import com.ssomcompany.ssomclient.common.Util;

public class CircularNetworkImageView extends SsomNetworkImageView {
    Context mContext;

    public CircularNetworkImageView(Context context) {
        super(context);
        mContext = context;
    }

    public CircularNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public CircularNetworkImageView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if(bm==null) return;
//        setImageDrawable(Util.getCircleBitmap(bm, bm.getWidth()));
        setImageDrawable(new RoundImage(Util.cropCenter(bm)));
    }
}
