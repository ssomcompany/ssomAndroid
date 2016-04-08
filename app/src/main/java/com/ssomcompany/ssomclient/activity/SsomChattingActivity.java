package com.ssomcompany.ssomclient.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.fragment.ChatListFragment;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.fragment.DetailFragment;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;

import java.util.ArrayList;

public class SsomChattingActivity extends BaseActivity implements ChatListFragment.OnChatItemInteractionListener {
    private static final String TAG = SsomChattingActivity.class.getSimpleName();
    public static final String EXTRA_KEY_CHAT_LIST = "chatList";

    private FragmentManager fragmentManager;
    private ArrayList<ChattingItem> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        fragmentManager = getSupportFragmentManager();
        // TODO call chatting list api
        chatList = (ArrayList<ChattingItem>) getIntent().getSerializableExtra(EXTRA_KEY_CHAT_LIST);

        SsomActionBarView ssomBar = (SsomActionBarView) findViewById(R.id.ssom_action_bar);
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHAT_LIST);
        ssomBar.setHeartLayoutVisibility(false);
        ssomBar.setSsomBarTitleVisibility(true);
        ssomBar.setSsomBarTitleGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarTitle(String.format(getResources().getString(R.string.chat_list_title), 0));
        // TODO MessageManager 등록하고 초기 message count 셋팅
        ssomBar.setChatIconOnOff(false);
        ssomBar.setChatCount("0");
        ssomBar.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startChatListFragment();
    }

    private void startChatListFragment() {
        ChatListFragment fragment = ChatListFragment.newInstance();
        fragment.setChatListData(chatList);
        fragmentManager.beginTransaction().
                replace(R.id.chat_container, fragment, CommonConst.CHAT_LIST_FRAG).commit();
    }

    @Override
    public void onChatItemClick(int position) {
        Log.i(TAG, "onPostItemClick() : " + position);

        ChattingFragment fragment = ChattingFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.chat_container, fragment, CommonConst.CHATTING_FRAG)
                .addToBackStack(CommonConst.CHAT_LIST_FRAG).commit();
    }
}
