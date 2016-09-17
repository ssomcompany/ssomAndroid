package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.network.NetworkManager;

/**
 * Created by AaronMac on 2016. 9. 17..
 */
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

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_image);
        imageView.setImageUrl(getIntent().getStringExtra(CommonConst.Intent.IMAGE_URL), NetworkManager.getInstance().getImageLoader());
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
