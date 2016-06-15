package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.SsomPreferences;

public class SsomChattingGuideActivity extends Activity {
    SsomPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_guide);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        pref = new SsomPreferences(this, SsomPreferences.CHATTING_PREF);

        findViewById(R.id.btn_chat_start).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pref.put(SsomPreferences.PREF_CHATTING_GUIDE_IS_READ, true);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
