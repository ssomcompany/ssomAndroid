package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;

import uk.co.senab.photoview.PhotoView;

public class SsomImageDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        if(getIntent() == null || getIntent().getExtras() == null
                || TextUtils.isEmpty(getIntent().getStringExtra(CommonConst.Intent.IMAGE_URL))) {
            Log.d(SsomImageDetailActivity.class.getSimpleName(), "intent is null, cannot start activity");
            finish();
            return;
        }

        PhotoView imageView = (PhotoView) findViewById(R.id.detail_image);
        Glide.with(this).load(getIntent().getStringExtra(CommonConst.Intent.IMAGE_URL))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
