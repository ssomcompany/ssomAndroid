package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.Util;

public class SsomActionBarView extends RelativeLayout {
    public static final String TAG = SsomActionBarView.class.getSimpleName();

    public static final int SSOM_MAIN_MENU = 0x01;
    public static final int SSOM_WRITE = 0x02;
    public static final int SSOM_CHAT_LIST = 0x03;
    public static final int SSOM_CHATTING = 0x04;
    public static final int SSOM_CONTACT_PEOPLE = 0x05;

    private int currentMode = SSOM_MAIN_MENU;

    private Context mContext;

    /* view settings */
    private RelativeLayout ssomActionBar;
    private ImageView btnLeftNavi;
    private RelativeLayout toggleView;
    private LinearLayout ssomBarTitleLayout;
    private TextView ssomBarTitle;
    private TextView ssomBarSubTitle;
    private FrameLayout chatLayout;
    private TextView chatCount;
    private LinearLayout heartLayout;
    private LinearLayout btnChattingRoomMeeting;
    private View imgMeetingHeart;
    private TextView tvChattingRoomMeeting;
    private TextView imgHeart;
    private View imgPlus;
    private TextView heartRefillTime;

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
        toggleView = (RelativeLayout) findViewById(R.id.toggle_bg);
        ssomBarTitleLayout = (LinearLayout) findViewById(R.id.ssom_bar_title_layout);
        ssomBarTitle = (TextView) findViewById(R.id.ssom_bar_title);
        ssomBarSubTitle = (TextView) findViewById(R.id.ssom_bar_sub_title);
        chatLayout = (FrameLayout) findViewById(R.id.chat_layout);
        chatCount = (TextView) findViewById(R.id.chat_count);
        heartLayout = (LinearLayout) findViewById(R.id.heart_layout);
        imgHeart = (TextView) findViewById(R.id.img_heart);
        imgPlus = findViewById(R.id.img_plus);
        heartRefillTime = (TextView) findViewById(R.id.heart_refill_time);
        btnChattingRoomMeeting = (LinearLayout) findViewById(R.id.btn_chatting_room_meeting);
        tvChattingRoomMeeting = (TextView) findViewById(R.id.tv_chatting_room_meeting);
        imgMeetingHeart = findViewById(R.id.img_meeting_heart);
    }

    private void initLayout() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        switch (currentMode) {
            case SSOM_MAIN_MENU :
                // set action bar height
                params.height = Util.convertDpToPixel(58.5f);
                ssomActionBar.setLayoutParams(params);
                btnLeftNavi.setImageResource(R.drawable.icon_top_menu);
                toggleView.setVisibility(View.VISIBLE);
                ssomBarTitleLayout.setVisibility(View.GONE);
                break;
            default:
                // set action bar height
                params.height = Util.convertDpToPixel(52f);
                ssomActionBar.setLayoutParams(params);
                btnLeftNavi.setImageResource(R.drawable.icon_back);
                toggleView.setVisibility(View.GONE);
                ssomBarTitleLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setOnLeftNaviBtnClickListener(View.OnClickListener listener) {
        if(btnLeftNavi != null) btnLeftNavi.setOnClickListener(listener);
    }

    public void setOnChattingBtnClickListener(View.OnClickListener listener) {
        if(chatLayout != null) chatLayout.setOnClickListener(listener);
    }

    public void setOnHeartBtnClickListener(View.OnClickListener listener) {
        if(heartLayout != null) heartLayout.setOnClickListener(listener);
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

    // right menu button settings
    /**
     * Call this method if want to set HeartLayout's visibility. (Default true)
     * @param visibility set true if want this is visible false otherwise
     */
    public void setHeartLayoutVisibility(boolean visibility) {
        heartLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setHeartLayoutGravity(int gravity, int additional) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.convertDpToPixel(39), LayoutParams.WRAP_CONTENT);
        params.addRule(gravity);
        params.addRule(additional);

        heartLayout.setLayoutParams(params);
    }

    /**
     * Call this method if want to set HeartLayout's icon. (Default off)
     * @param iconOnOff set true if want this is on false otherwise
     */
    public void setHeartIconOnOff(boolean iconOnOff) {
        imgHeart.setBackgroundResource(iconOnOff ? R.drawable.top_heart : R.drawable.top_heart_gray);
        imgPlus.setBackgroundResource(iconOnOff ? R.drawable.top_plus : R.drawable.top_plus_gray);
    }

    public void setHeartCount(int count) {
        imgHeart.setText(String.valueOf(count));
        setHeartIconOnOff(count != 0);
    }

    public void setHeartRefillTime(String time) {
        heartRefillTime.setText(time);
    }

    /**
     * Call this method if want to set ChatLayout's visibility. (Default true)
     * @param visibility set true if want this is visible false otherwise
     */
    public void setChatLayoutVisibility(boolean visibility) {
        chatLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * Call this method if want to set ChatLayout's icon. (Default off)
     * @param iconOnOff set true if want this is on false otherwise
     */
    public void setChatIconOnOff(boolean iconOnOff) {
        chatLayout.setBackgroundResource(iconOnOff ? R.drawable.top_message : R.drawable.top_message_gray);
    }

    public void setChatCount(String count) {
        chatCount.setText(count);
    }

    public int getChatCount() {
        return Integer.parseInt(String.valueOf(chatCount.getText()));
    }

    public void setChattingRoomBtnMeetingVisibility(boolean visibility) {
        btnChattingRoomMeeting.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setChattingRoomBtnMeetingOnOff(boolean iconOnOff) {
        btnChattingRoomMeeting.setBackgroundResource(iconOnOff ? R.drawable.btn_write_apply_ssoa : R.drawable.btn_chat_info_cancel);
    }

    public void setChattingRoomBtnMeetingTitle(String title) {
        tvChattingRoomMeeting.setText(title);
    }

    public void setChattingRoomMeetingHeartVisibility(boolean visibility) {
        imgMeetingHeart.setVisibility(visibility ? View.VISIBLE : View.GONE);
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
}
