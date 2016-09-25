package com.ssomcompany.ssomclient.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.ChatRoomListFragment;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.CreateChattingRoom;
import com.ssomcompany.ssomclient.network.api.GetChattingRoomList;
import com.ssomcompany.ssomclient.network.api.SsomChatUnreadCount;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;

public class SsomChattingActivity extends BaseActivity implements ViewListener.OnChatItemInteractionListener, MessageCountCheck {
    private static final String TAG = SsomChattingActivity.class.getSimpleName();

    private static final int STATE_CHAT_LIST = 0;
    private static final int STATE_CHAT_ROOM = 1;

    private static int CURRENT_STATE = STATE_CHAT_LIST;

    // fragment instance 저장
    private ChatRoomListFragment roomListFragment;

    private FragmentManager fragmentManager;
    private SsomActionBarView ssomBar;

    private SsomPreferences chatPref;
    private InputMethodManager imm;

    private int unreadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fragmentManager = getSupportFragmentManager();
        chatPref = new SsomPreferences(this, SsomPreferences.CHATTING_PREF);
        ssomBar = (SsomActionBarView) findViewById(R.id.ssom_action_bar);
        initSsomBarView();
        initIntentData();
    }

    private void initIntentData() {
        if(getIntent() != null && getIntent().getExtras() != null) {
            final SsomItem ssomItem = (SsomItem) getIntent().getSerializableExtra(CommonConst.Intent.SSOM_ITEM);

            APICaller.createChattingRoom(getToken(), ssomItem.getPostId(),
                    new NetworkManager.NetworkListener<SsomResponse<CreateChattingRoom.Response>>() {
                @Override
                public void onResponse(SsomResponse<CreateChattingRoom.Response> response) {
                    if(response.isSuccess()) {
                        ChatRoomItem chatRoomItem = new ChatRoomItem();
                        chatRoomItem.setId(response.getData().getChatroomId());
                        chatRoomItem.setInfoType(ChatRoomItem.InfoType.none);
                        chatRoomItem.setUserId(ssomItem.getUserId());
                        chatRoomItem.setImageUrl(ssomItem.getImageUrl());
                        chatRoomItem.setSsomType(ssomItem.getSsomType());

                        CURRENT_STATE = STATE_CHAT_ROOM;
                        startChattingFragment(chatRoomItem);
                        changeSsomBarViewForChattingRoom(ssomItem.getMinAge(), ssomItem.getUserCount());
//                        if(response.getStatusCode() == 304) {
//                            Log.d(TAG, "chat already exist : " + response.getData().getChatroomId());
//                            startChattingFragment(response.getData().getList().get(0));
//                        } else {
//                            Log.d(TAG, "chat began");
//                        }
                    } else {
                        showErrorMessage();
                    }
                }
            });
        } else {
            startChatRoomListFragment();
        }
    }

    private void initSsomBarView() {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHAT_LIST);
        ssomBar.setHeartLayoutVisibility(false);
        ssomBar.setChatLayoutVisibility(false);
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        APICaller.totalChatUnreadCount(getToken(), new NetworkManager.NetworkListener<SsomResponse<SsomChatUnreadCount.Response>>() {
            @Override
            public void onResponse(SsomResponse<SsomChatUnreadCount.Response> response) {
                if(response.isSuccess() && response.getData() != null) {
                    unreadCount = response.getData().getUnreadCount();
                    ssomBar.setSsomBarTitleText(String.format(getResources().getString(R.string.chat_list_title), unreadCount));
                } else {
                    showErrorMessage();
                }
            }
        });
        ssomBar.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (CURRENT_STATE) {
                    case STATE_CHAT_LIST :
                        finish();
                        break;
                    case STATE_CHAT_ROOM :
//                        if(getCurrentFocus() != null)
//                            imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        CURRENT_STATE = STATE_CHAT_LIST;
                        changeSsomBarViewForChatRoomList();
                        startChatRoomListFragment();
                        hideSoftKeyboard();
                }
            }
        });
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void changeSsomBarViewForChatRoomList() {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHAT_LIST);
        ssomBar.setChattingRoomHeartVisibility(false);
        ssomBar.setChatLayoutVisibility(false);
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleText(String.format(getResources().getString(R.string.chat_list_title), unreadCount));
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarSubTitleVisibility(false);
    }

    private void changeSsomBarViewForChattingRoom(int minAge, int userCount) {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHATTING);
        ssomBar.setChatLayoutVisibility(false);
        ssomBar.setChattingRoomHeartVisibility(true);
        // TODO heart count 셋팅 정의
        ssomBar.setChattingRoomHeartOnOff(true);
        ssomBar.setChattingRoomHeartText(String.valueOf(5));
        ssomBar.setOnChattingRoomHeartBtnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO 하트가 없는 경우 체크
                UiUtils.makeCommonDialog(SsomChattingActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                        R.string.dialog_meet_request, R.style.ssom_font_20_grayish_brown_bold,
                        R.string.dialog_meet_request_message, 0, R.string.dialog_meet, R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO call accept api (만남 요청)
                        String userId = ((ChattingFragment) getSupportFragmentManager().findFragmentById(R.id.chat_container)).getChatRoomUserId();
                        Toast.makeText(getApplicationContext(), userId + "에게 쏨을 요청함.", Toast.LENGTH_SHORT).show();
                        // TODO userId 로 상대방에게 쏨 요청
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO call reject api (만남 거절, 요청 안 취소)
                    }
                });
            }
        });
        // TODO MessageManager 해제
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleText(getResources().getString(R.string.chat_room_title));
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarSubTitleVisibility(true);
        ssomBar.setSsomBarSubTitleText(String.format(getResources().getString(R.string.filter_age_n_count),
                Util.convertAgeRange(minAge), Util.convertPeopleRange(userCount)));
        ssomBar.setSsomBarSubTitleStyle(R.style.ssom_font_12_pinkish_gray_two_single);
    }

    private void startChatRoomListFragment() {
        if(roomListFragment == null) roomListFragment = new ChatRoomListFragment();
        fragmentManager.beginTransaction().replace(R.id.chat_container, roomListFragment, CommonConst.CHAT_LIST_FRAG).commit();
//        fragmentManager.executePendingTransactions();
    }

    private void startChattingFragment(ChatRoomItem chatRoomItem) {
        ChattingFragment fragment = ChattingFragment.newInstance(chatPref.getBoolean(SsomPreferences.PREF_CHATTING_GUIDE_IS_READ, false));
        fragment.setChatRoomItem(chatRoomItem);
        fragmentManager.beginTransaction().replace(R.id.chat_container, fragment, CommonConst.CHATTING_FRAG).commit();
    }

    @Override
    public void onChatItemClick(final int position) {
        CURRENT_STATE = STATE_CHAT_ROOM;
        ChatRoomItem chatRoom = roomListFragment.getChatRoomList().get(position);
        startChattingFragment(chatRoom);
        changeSsomBarViewForChattingRoom(chatRoom.getMinAge(),
                chatRoom.getUserCount());
    }

    @Override
    public void onBackPressed() {
        if(imm.hideSoftInputFromWindow(null, 0)) {
            return;
        }

        if(CURRENT_STATE == STATE_CHAT_ROOM) {
            CURRENT_STATE = STATE_CHAT_LIST;
            changeSsomBarViewForChatRoomList();
            startChatRoomListFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void receivedPushMessage(Intent intent) {
        super.receivedPushMessage(intent);
        Log.d(TAG, "receivedPushMessage............");

        if(fragmentManager == null) {
            Log.d(TAG, "fragmentManager is null");
            return;
        }

        if(intent == null) {
            Log.d(TAG, "intent is null");
            return;
        }

        if(fragmentManager.findFragmentById(R.id.chat_container) instanceof ChatRoomListFragment) {
            // chatting room 화면을 보고 있는 경우에 채팅룸 갱신
            ArrayList<ChatRoomItem> roomList = roomListFragment.getChatRoomList();
            int chatRoomId = Integer.parseInt(intent.getStringExtra(CommonConst.Intent.CHAT_ROOM_ID));
            int roomIndex = 0;

            for(int i = 0 ; i < roomList.size() ; i++) {
                if(roomList.get(i).getId() == chatRoomId) {
                    roomIndex = i;
                    break;
                }
            }
            ChatRoomItem tempItem = roomList.get(roomIndex);
            tempItem.setLastMsg(intent.getStringExtra(CommonConst.Intent.MESSAGE));
            tempItem.setUnreadCount(tempItem.getUnreadCount() + 1);
            roomList.remove(roomIndex);
            roomList.add(0, tempItem);
            roomListFragment.setChatRoomListAndNotify(roomList);
            ssomBar.setSsomBarTitleText(String.format(getResources().getString(R.string.chat_list_title), ++unreadCount));
        } else {
            // 푸시를 받은 방에서 chatting 중이라면 메시지를 추가해서 보여줌
            ChattingFragment chatting = (ChattingFragment) fragmentManager.findFragmentByTag(CommonConst.CHATTING_FRAG);
            if(chatting.getChatRoomId().equalsIgnoreCase(intent.getStringExtra(CommonConst.Intent.CHAT_ROOM_ID))) {
                ChattingItem chat = new ChattingItem();
                chat.setMsg(intent.getStringExtra(CommonConst.Intent.MESSAGE));
                chat.setFromUserId(intent.getStringExtra(CommonConst.Intent.FROM_USER_ID));
                chat.setToUserId(intent.getStringExtra(CommonConst.Intent.TO_USER_ID));
                chat.setTimestamp(intent.getLongExtra(CommonConst.Intent.TIMESTAMP, 0));
                chatting.addChatting(chat);
            } else {
                unreadCount++;
            }
        }
    }
}
