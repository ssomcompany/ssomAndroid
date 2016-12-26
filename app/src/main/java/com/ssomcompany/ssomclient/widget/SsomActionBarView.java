package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.AddHeartCount;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageManager;

public class SsomActionBarView extends Toolbar {
    public static final String TAG = SsomActionBarView.class.getSimpleName();

    public static final int SSOM_MAIN_MENU = 0x01;
    public static final int SSOM_WRITE = 0x02;
    public static final int SSOM_CHAT_LIST = 0x03;
    public static final int SSOM_CHATTING = 0x04;
    public static final int SSOM_CONTACT_PEOPLE = 0x05;
    public static final int SSOM_MAP = 0x06;

    private int currentMode = SSOM_MAIN_MENU;

    private CountDownTimer timerTask;
    private boolean timerIsRunning;

    private Context mContext;

    /* view settings */
    private RelativeLayout ssomActionBar;
    private ImageView btnLeftNavi;
    private LinearLayout ssomBarTitleLayout;
    private TextView ssomBarTitle;
    private TextView ssomBarSubTitle;
    private TextView btnChattingRoomMeeting;

    public SsomActionBarView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context, null);
        }
    }

    public SsomActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public SsomActionBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        setView(context);
        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CommonActionBarAttr);
            currentMode = typedArray.getInt(R.styleable.CommonActionBarAttr_commonActionBarStyle, SSOM_MAIN_MENU);
            typedArray.recycle();
        }
        initLayout();
    }

    private void setView(Context context) {
        mContext = context;

        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.view_ssom_action_bar, this, false);
        addView(view);

        ssomActionBar = (RelativeLayout) findViewById(R.id.ssom_action_bar);
        btnLeftNavi = (ImageView) findViewById(R.id.btn_left_navi);
        ssomBarTitleLayout = (LinearLayout) findViewById(R.id.ssom_bar_title_layout);
        ssomBarTitle = (TextView) findViewById(R.id.ssom_bar_title);
        ssomBarSubTitle = (TextView) findViewById(R.id.ssom_bar_sub_title);
        btnChattingRoomMeeting = (TextView) findViewById(R.id.btn_chatting_room_meeting);
    }

    private void initLayout() {
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        switch (currentMode) {
            case SSOM_MAIN_MENU :
                // set action bar height
                params.height = Util.convertDpToPixel(35f);
                ssomActionBar.setLayoutParams(params);
                btnLeftNavi.setImageResource(R.drawable.icon_top_menu);
                break;
            default:
                // set action bar height
                params.height = Util.convertDpToPixel(52f);
                ssomActionBar.setLayoutParams(params);
                btnLeftNavi.setImageResource(R.drawable.icon_back);
                break;
        }
    }

    public void setOnLeftNaviBtnClickListener(View.OnClickListener listener) {
        if(btnLeftNavi != null) btnLeftNavi.setOnClickListener(listener);
    }

    public void setOnChattingRoomMeetingBtnClickListener(View.OnClickListener listener) {
        if(btnChattingRoomMeeting != null) btnChattingRoomMeeting.setOnClickListener(listener);
    }

    // ssom bar title settings
    /**
     * Call this method if want to set SsomBarTitle's visibility. (Default false)
     * @param visibility set true if want this is visible false otherwise
     */
    public void setSsomBarTitleLayoutVisibility(boolean visibility) {
        ssomBarTitleLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setSsomBarTitleLayoutGravity(int gravity) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(gravity);

        ssomBarTitleLayout.setLayoutParams(params);
    }

    /**
     * title drawable 과 padding 설정
     * @param resId drawable resources id
     * @param padding left padding in pixel
     */
    public void setSsomBarTitleDrawable(int resId, int padding) {
        ssomBarTitle.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        ssomBarTitle.setCompoundDrawablePadding(padding);
    }

    public void setSsomBarTitleText(String title) {
        ssomBarTitle.setText(title);
    }

    public void setSsomBarTitleStyle(int styId) {
        ssomBarTitle.setTextAppearance(mContext, styId);
    }

    public void setSsomBarSubTitleVisibility(boolean visibility) {
        ssomBarSubTitle.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setSsomBarSubTitleText(String title) {
        ssomBarSubTitle.setText(title);
    }

    public void setSsomBarSubTitleStyle(int styId) {
        ssomBarSubTitle.setTextAppearance(mContext, styId);
    }

    public void setChattingRoomBtnMeetingVisibility(boolean visibility) {
        btnChattingRoomMeeting.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setChattingRoomBtnMeetingOnOff(boolean iconOnOff) {
        btnChattingRoomMeeting.setBackgroundResource(iconOnOff ? R.drawable.btn_write_apply_ssoa : R.drawable.btn_chat_info_cancel);
    }

    public void setChattingRoomBtnMeetingTitle(String title) {
        btnChattingRoomMeeting.setText(title);
    }

    public int getCurrentMode() {
        return currentMode;
    }

    /**
     * Call this method if want to set mode. (Default SSOM_MAIN_MENU)
     * @param currentMode set this view's int variable if wanna set mode
     */
    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
        initLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(TAG, "onDetachedFromWindow called");

        if(timerTask != null && timerIsRunning) {
            timerTask.cancel();
        }
    }
}
