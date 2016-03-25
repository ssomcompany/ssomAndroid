package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.android.volley.toolbox.NetworkImageView;
import com.ssomcompany.ssomclient.common.Util;

public class RoundedNetworkImageView extends NetworkImageView {
    private static final String TAG = RoundedNetworkImageView.class.getSimpleName();

    private Path clipPath = new Path();
    private RectF rectF = new RectF();

    public RoundedNetworkImageView(Context context) {
        this(context, (AttributeSet) null);
        Log.d(TAG, "RoundedNetworkImageView(context) called!");
    }

    public RoundedNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        Log.d(TAG, "RoundedNetworkImageView(context, attrs) called!");
    }

    public RoundedNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "RoundedNetworkImageView(context, attrs, defStyle) called!");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw()");
        float radius = Util.convertDpToPixel(25);
        // 정석. 제공되는 api
        float[] radii = {/* pair of top left x,y */ radius, radius,
                        /* pair of top right x,y */ radius, radius, /* former 4 params for top radius */
                        /* pair of bottom left x,y */ 0, 0,
                        /* pair of bottom right x,y */ 0, 0}; /* footer 4 params for bottom radius */

        // 아래 주석 코드(편법) height 를 강제로 높여 아래를 보이지 않게 함.
//        rectF.set(0, 0, this.getWidth(), this.getHeight() + radius);
//        clipPath.addRoundRect(rectF, radius, radius, Path.Direction.CW);

        rectF.set(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rectF, radii, Path.Direction.CW);
        canvas.clipPath(clipPath);

        super.onDraw(canvas);
    }
}