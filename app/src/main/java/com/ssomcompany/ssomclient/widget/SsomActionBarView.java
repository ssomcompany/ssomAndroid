package com.ssomcompany.ssomclient.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private TextView ssomBarTitle;
    private TextView chatLayout;
    private LinearLayout heartLayout;
    private ImageView imgHeart;
    private TextView heartCount;
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
        ssomBarTitle = (TextView) findViewById(R.id.ssom_bar_title);
        chatLayout = (TextView) findViewById(R.id.chat_layout);
        heartLayout = (LinearLayout) findViewById(R.id.heart_layout);
        imgHeart = (ImageView) findViewById(R.id.img_heart);
        heartCount = (TextView) findViewById(R.id.heart_count);
        heartRefillTime = (TextView) findViewById(R.id.heart_refill_time);
    }

    private void initLayout() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        switch (currentMode) {
            case SSOM_MAIN_MENU :
                // set action bar height
                params.height = (int) Util.convertDpToPixel(58.5f);
                ssomActionBar.setLayoutParams(params);
                btnLeftNavi.setImageResource(R.drawable.icon_top_menu);
                toggleView.setVisibility(View.VISIBLE);
                ssomBarTitle.setVisibility(View.GONE);
                break;
            default:
                // set action bar height
                params.height = (int) Util.convertDpToPixel(52f);
                ssomActionBar.setLayoutParams(params);
                btnLeftNavi.setImageResource(R.drawable.icon_back);
                toggleView.setVisibility(View.GONE);
                ssomBarTitle.setVisibility(View.VISIBLE);
                break;
        }
    }

    // ssom bar title settings
    /**
     * Call this method if want to set SsomBarTitle's visibility. (Default false)
     * @param visibility set true if want this is visible false otherwise
     */
    public void setSsomBarTitleVisibility(boolean visibility) {
        ssomBarTitle.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setSsomBarTitle(String title) {
        ssomBarTitle.setText(title);
    }

    public void setSsomBarTitleStyle(int styId) {
        ssomBarTitle.setTextAppearance(mContext, styId);
    }

    public void setSsomBarTitleGravity(int gravity) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(gravity);

        ssomBarTitle.setLayoutParams(params);
    }

    // right menu button settings
    /**
     * Call this method if want to set HeartLayout's visibility. (Default true)
     * @param visibility set true if want this is visible false otherwise
     */
    public void setHeartLayoutVisibility(boolean visibility) {
        heartLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * Call this method if want to set HeartLayout's icon. (Default off)
     * @param iconOnOff set true if want this is on false otherwise
     */
    public void setHeartIconOnOff(boolean iconOnOff) {
        imgHeart.setImageResource(iconOnOff ? R.drawable.icon_heart_red : R.drawable.icon_heart);
    }

    public void setHeartCount(int count) {
        heartCount.setText(String.valueOf(count));
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
    @SuppressLint("NewApi")
    public void setChatIconOnOff(final boolean iconOnOff) {
        chatLayout.setCompoundDrawablesWithIntrinsicBounds(0, iconOnOff ? R.drawable.icon_chat_red : R.drawable.icon_chat, 0, 0);
    }

    public void setChatCount(String count) {
        chatLayout.setText(count);
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
