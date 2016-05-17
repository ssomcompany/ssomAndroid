package com.ssomcompany.ssomclient.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.fragment.ChatRoomListFragment;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;

import java.util.ArrayList;

public class SsomChattingActivity extends BaseActivity implements ChatRoomListFragment.OnChatItemInteractionListener {
    private static final String TAG = SsomChattingActivity.class.getSimpleName();

    private static final int STATE_CHAT_LIST = 0;
    private static final int STATE_CHAT_ROOM = 1;

    private static int CURRENT_STATE = STATE_CHAT_LIST;

    private FragmentManager fragmentManager;
    private ArrayList<ChatRoomItem> chatRoomList;
    private SsomActionBarView ssomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        fragmentManager = getSupportFragmentManager();
        // TODO call chatting list api
        chatRoomList = new ArrayList<>();
        ChatRoomItem item;
        double x = 0;
        int count = 0;
        for(int i = 0 ; i < 10 ; i++) {
            item = new ChatRoomItem();
            item.setPostId(String.valueOf(System.currentTimeMillis()));
            item.setContent("123123");
            item.setImageUrl("");
            item.setMinAge(25);
            item.setSsom("ssom");
            item.setUserCount(3);
            item.setLastMessage("test chatting message " + count);
            item.setLongitude(127.1020387+x);
            item.setLatitude(37.5160211+x);
            if(i == 3) item.setInfoType(ChatRoomItem.InfoType.sent);
            if(i == 5) item.setInfoType(ChatRoomItem.InfoType.received);
            if(i == 7) item.setInfoType(ChatRoomItem.InfoType.success);
            chatRoomList.add(item);
            x += 0.001;
            count++;
        }

        ssomBar = (SsomActionBarView) findViewById(R.id.ssom_action_bar);
        initSsomBarView();
        startChatRoomListFragment();
    }

    private void initSsomBarView() {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHAT_LIST);
        ssomBar.setHeartLayoutVisibility(false);
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        // TODO MessageManager 등록하고 초기 message count 셋팅
        ssomBar.setSsomBarTitleText(String.format(getResources().getString(R.string.chat_list_title), 0));
        ssomBar.setChatIconOnOff(false);
        ssomBar.setChatCount("0");
        ssomBar.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (CURRENT_STATE) {
                    case STATE_CHAT_LIST :
                        finish();
                        break;
                    case STATE_CHAT_ROOM :
                        CURRENT_STATE = STATE_CHAT_LIST;
                        changeSsomBarViewForChatRoomList();
                        fragmentManager.popBackStack();
                }
            }
        });
    }

    private void changeSsomBarViewForChatRoomList() {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHAT_LIST);
        ssomBar.setChattingRoomHeartVisibility(false);
        ssomBar.setChatLayoutVisibility(true);
        // TODO MessageManager 등록하고 초기 message count 셋팅
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleText(String.format(getResources().getString(R.string.chat_list_title), 0));
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarSubTitleVisibility(false);
    }

    private void changeSsomBarViewForChattingRoom(int position) {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHATTING);
        ssomBar.setChatLayoutVisibility(false);
        ssomBar.setChattingRoomHeartVisibility(true);
        // TODO heart count 셋팅 정의
        ssomBar.setChattingRoomHeartOnOff(true);
        ssomBar.setChattingRoomHeartText(String.valueOf(5));
        // TODO MessageManager 해제
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleText(getResources().getString(R.string.chat_room_title));
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarSubTitleVisibility(true);
        ssomBar.setSsomBarSubTitleText(String.format(getResources().getString(R.string.filter_age_n_count),
                Util.convertAgeRange(chatRoomList.get(position).getMinAge()), Util.convertPeopleRange(chatRoomList.get(position).getUserCount())));
        ssomBar.setSsomBarSubTitleStyle(R.style.ssom_font_12_pinkish_gray_two_single);
    }

    private void startChatRoomListFragment() {
        ChatRoomListFragment fragment = ChatRoomListFragment.newInstance();
        fragment.setChatRoomListData(chatRoomList);
        fragmentManager.beginTransaction().
                replace(R.id.chat_container, fragment, CommonConst.CHAT_LIST_FRAG).commit();
//        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onChatItemClick(int position) {
        CURRENT_STATE = STATE_CHAT_ROOM;
        changeSsomBarViewForChattingRoom(position);

        ChattingFragment fragment = new ChattingFragment();
        fragment.setChatRoomItem(chatRoomList.get(position));
        fragmentManager.beginTransaction().replace(R.id.chat_container, fragment, CommonConst.CHATTING_FRAG)
                .addToBackStack(CommonConst.CHAT_LIST_FRAG).commit();
//        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if(CURRENT_STATE == STATE_CHAT_ROOM) {
            CURRENT_STATE = STATE_CHAT_LIST;
            changeSsomBarViewForChatRoomList();
        }
        super.onBackPressed();
    }
}
