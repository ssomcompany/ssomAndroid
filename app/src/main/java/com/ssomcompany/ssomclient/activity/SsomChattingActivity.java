package com.ssomcompany.ssomclient.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.ChatRoomListFragment;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.CreateChattingRoom;
import com.ssomcompany.ssomclient.network.api.GetChattingRoomList;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;

public class SsomChattingActivity extends BaseActivity implements ViewListener.OnChatItemInteractionListener {
    private static final String TAG = SsomChattingActivity.class.getSimpleName();

    private static final int STATE_CHAT_LIST = 0;
    private static final int STATE_CHAT_ROOM = 1;

    private static int CURRENT_STATE = STATE_CHAT_LIST;

    private FragmentManager fragmentManager;
    private ArrayList<ChatRoomItem> chatRoomList;
    private SsomActionBarView ssomBar;

    private SsomPreferences chatPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        fragmentManager = getSupportFragmentManager();
        chatPref = new SsomPreferences(this, SsomPreferences.CHATTING_PREF);
        ssomBar = (SsomActionBarView) findViewById(R.id.ssom_action_bar);
        initIntentData();
        initSsomBarView();
        requestChattingList();
    }

    private void initIntentData() {
        if(getIntent() != null && getIntent().getExtras() != null) {
            String postId = getIntent().getStringExtra(CommonConst.Intent.POST_ID);
            String userId = getIntent().getStringExtra(CommonConst.Intent.USER_ID);

            APICaller.createChattingRoom(getToken(), postId,
                    new NetworkManager.NetworkListener<SsomResponse<CreateChattingRoom.Response>>() {
                @Override
                public void onResponse(SsomResponse<CreateChattingRoom.Response> response) {
                    if(response.isSuccess()) {
                        Log.d(TAG, "response : " + response);
                    } else {
                        // TODO 이미 방이 있는 경우 조회로 이동
//                            startChattingFragment();
                    }
                }
            });
        }
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
        ssomBar.setOnChattingRoomHeartBtnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO 하트가 없는 경우 체크
                makeCommonDialog();
            }
        });
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

    private void requestChattingList() {
        showProgressDialog();
        APICaller.getChattingRoomList(getToken(), new NetworkManager.NetworkListener<SsomResponse<GetChattingRoomList.Response>>() {
            @Override
            public void onResponse(SsomResponse<GetChattingRoomList.Response> response) {
                if(response.isSuccess()) {
                    if(response.getData() != null) {
                        chatRoomList = response.getData().getChattingRoomList();
                        startChatRoomListFragment();
                    } else {
                        Log.e(TAG, "unexpected error, data is null");
                    }
                    dismissProgressDialog();
                } else {
                    dismissProgressDialog();

                    if(response.getStatusCode() == 404) {
                        // chat room 없는 사람
                        //TODO show empty list
                        return;
                    }

                    showErrorMessage();
                    finish();
                }
            }
        });
    }

    @Override
    public void onChatItemClick(int position) {
        CURRENT_STATE = STATE_CHAT_ROOM;
        changeSsomBarViewForChattingRoom(position);
        startChattingFragment(chatRoomList.get(position));
    }

    private void startChattingFragment(ChatRoomItem chatRoomItem) {
        ChattingFragment fragment = ChattingFragment.newInstance(chatPref.getBoolean(SsomPreferences.PREF_CHATTING_GUIDE_IS_READ, false));
        fragment.setChatRoomItem(chatRoomItem);
        fragmentManager.beginTransaction().replace(R.id.chat_container, fragment, CommonConst.CHATTING_FRAG)
                .addToBackStack(CommonConst.CHAT_LIST_FRAG).commit();
    }

    @Override
    public void onBackPressed() {
        if(CURRENT_STATE == STATE_CHAT_ROOM) {
            CURRENT_STATE = STATE_CHAT_LIST;
            changeSsomBarViewForChatRoomList();
        }
        super.onBackPressed();
    }

    private void makeCommonDialog() {
        CommonDialog dialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_ALERT_BUTTON);
        dialog.setTitle(getString(R.string.dialog_meet_request));
        dialog.setTitleStyle(R.style.ssom_font_20_grayish_brown_bold);
        dialog.setMessage(getString(R.string.dialog_meet_request_message));
        dialog.setPositiveButton(getString(R.string.dialog_meet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO call accept api (만남 요청)
                String userId = ((ChattingFragment) getSupportFragmentManager().findFragmentById(R.id.chat_container)).getChatRoomUserId();
                Toast.makeText(getApplicationContext(), userId + "에게 쏨을 요청함.", Toast.LENGTH_SHORT).show();
                // TODO userId 로 상대방에게 쏨 요청
            }
        });
        dialog.setNegativeButton(getString(R.string.dialog_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO call reject api (만남 거절, 요청 안 취소)
                    }
                });
        dialog.setAutoDismissEnable(true);
        dialog.show(getFragmentManager(), null);
    }
}
