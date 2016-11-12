package com.ssomcompany.ssomclient.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.ChatRoomListFragment;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.CreateChattingRoom;
import com.ssomcompany.ssomclient.network.api.SsomChatUnreadCount;
import com.ssomcompany.ssomclient.network.api.SsomMeetingRequest;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.MessageManager;
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
    private ChatRoomItem chatRoomItem;
    private ChattingFragment chattingFragment;

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
                    if(response.isSuccess() && response.getData() != null) {
                        chatRoomItem = new ChatRoomItem();
                        chatRoomItem.setId(response.getData().getChatroomId());
                        chatRoomItem.setOwnerId(getUserId());
                        chatRoomItem.setOwnerImageUrl(getTodayImageUrl());
                        chatRoomItem.setParticipantId(ssomItem.getUserId());
                        chatRoomItem.setParticipantImageUrl(ssomItem.getImageUrl());
                        chatRoomItem.setSsomType(ssomItem.getSsomType());
                        chatRoomItem.setUserCount(ssomItem.getUserCount());
                        chatRoomItem.setMinAge(ssomItem.getMinAge());
                        chatRoomItem.setLongitude(ssomItem.getLongitude());
                        chatRoomItem.setLatitude(ssomItem.getLatitude());
                        chatRoomItem.setPostId(ssomItem.getPostId());
                        chatRoomItem.setCreatedTimestamp(response.getData().getCreatedTimestamp());

                        CURRENT_STATE = STATE_CHAT_ROOM;
                        startChattingFragment();
                        changeSsomBarViewForChattingRoom();
                        getSession().put(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, System.currentTimeMillis());
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
        ssomBar.setSsomBarTitleText(getString(R.string.chat_list_title_empty));
        APICaller.totalChatUnreadCount(getToken(), new NetworkManager.NetworkListener<SsomResponse<SsomChatUnreadCount.Response>>() {
            @Override
            public void onResponse(SsomResponse<SsomChatUnreadCount.Response> response) {
                if(response.isSuccess() && response.getData() != null) {
                    unreadCount = response.getData().getUnreadCount();
                    if(unreadCount != 0) ssomBar.setSsomBarTitleText(String.format(getString(R.string.chat_list_title), unreadCount));
                } else {
                    showErrorMessage();
                }
            }
        });
        ssomBar.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        ssomBar.setChattingRoomBtnMeetingVisibility(false);
        ssomBar.setChatLayoutVisibility(false);
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleText(unreadCount == 0 ?
                getString(R.string.chat_list_title_empty) : String.format(getString(R.string.chat_list_title), unreadCount));
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarSubTitleVisibility(false);
    }

    private void changeSsomBarViewForChattingRoom() {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHATTING);
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleText(getString(R.string.chat_room_title));
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarSubTitleVisibility(true);
        ssomBar.setSsomBarSubTitleText((chatRoomItem.getMinAge() | chatRoomItem.getUserCount()) == 0 ? getString(R.string.chat_room_no_info) :
                String.format(getString(R.string.filter_age_n_count), Util.convertAgeRange(chatRoomItem.getMinAge()), Util.convertPeopleRange(chatRoomItem.getUserCount())));
        ssomBar.setSsomBarSubTitleStyle(R.style.ssom_font_12_pinkish_gray_two_single);
        ssomBar.setChatLayoutVisibility(false);

        // 채팅룸 우측 상단의 버튼을 기능에 맞게 적용
        setMeetingButton();
    }

    private void setMeetingButton() {
        if((chatRoomItem.getMinAge() | chatRoomItem.getUserCount()) == 0 ||
                CommonConst.Chatting.MEETING_OUT.equalsIgnoreCase(chatRoomItem.getStatus()) ||
                (CommonConst.Chatting.SYSTEM.equals(chatRoomItem.getLastMsgType()) &&
                        CommonConst.Chatting.MEETING_OUT.equalsIgnoreCase(chatRoomItem.getLastMsg()))) {
            chatRoomItem.setStatus(CommonConst.Chatting.MEETING_OUT);
            chattingFragment.setChatRoomItem(chatRoomItem);
            ssomBar.setChattingRoomBtnMeetingVisibility(false);
        } else {
            ssomBar.setChattingRoomBtnMeetingVisibility(true);
            final int dialogMsg;
            final int dialogOkBtn;
            final int dialogNoBtn;
            final int methodType;

            if (CommonConst.Chatting.MEETING_REQUEST.equals(chatRoomItem.getStatus())) {
                boolean isMyRequest = getUserId().equals(chatRoomItem.getRequestId());
                // 내가 요청한거면 요청취소, 내가 요청한게 아니면 만남수락
                ssomBar.setChattingRoomBtnMeetingOnOff(!isMyRequest);
                ssomBar.setChattingRoomBtnMeetingTitle(isMyRequest ?
                        getString(R.string.chat_room_info_btn_sent) : getString(R.string.dialog_meet_apply));
                methodType = isMyRequest ? NetworkConstant.Method.DELETE : NetworkConstant.Method.PUT;
                dialogMsg = isMyRequest ? R.string.dialog_meet_request_cancel_message : R.string.dialog_meet_received_message;
                dialogOkBtn = isMyRequest ? R.string.chat_room_info_btn_sent : R.string.dialog_meet_apply;
                dialogNoBtn = isMyRequest ? R.string.dialog_close : R.string.dialog_not_now;
            } else if(CommonConst.Chatting.MEETING_APPROVE.equals(chatRoomItem.getStatus())) {
                // 만남 중이니 서로 만남취소 가능
                ssomBar.setChattingRoomBtnMeetingOnOff(false);
                ssomBar.setChattingRoomBtnMeetingTitle(getString(R.string.dialog_meet_finish));
                methodType = NetworkConstant.Method.DELETE;
                dialogMsg = R.string.dialog_meet_finish_message;
                dialogOkBtn = R.string.dialog_meet_finish;
                dialogNoBtn = R.string.dialog_close;
            } else {
                ssomBar.setChattingRoomBtnMeetingOnOff(true);
                ssomBar.setChattingRoomBtnMeetingTitle(getString(R.string.dialog_meet_request));
                methodType = NetworkConstant.Method.POST;
                dialogMsg = R.string.dialog_meet_request_message;
                dialogOkBtn = R.string.dialog_meet;
                dialogNoBtn = R.string.dialog_cancel;
            }

            ssomBar.setOnChattingRoomMeetingBtnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    UiUtils.makeCommonDialog(SsomChattingActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                            R.string.dialog_notice, R.style.ssom_font_20_red_pink_bold, dialogMsg, 0, dialogOkBtn, dialogNoBtn,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    APICaller.sendChattingRequest(getToken(), chatRoomItem.getId(), methodType,
                                            new NetworkManager.NetworkListener<SsomResponse<SsomMeetingRequest.Response>>() {
                                                @Override
                                                public void onResponse(SsomResponse<SsomMeetingRequest.Response> response) {
                                                    if(response.isSuccess()) {
                                                        switch (methodType) {
                                                            case NetworkConstant.Method.PUT:
                                                                chatRoomItem.setRequestId(getUserId());
                                                                chatRoomItem.setStatus(CommonConst.Chatting.MEETING_APPROVE);
                                                                chattingFragment.addChatting(new ChattingItem()
                                                                        .setStatus(ChattingItem.MessageType.approve)
                                                                        .setMsgType(CommonConst.Chatting.SYSTEM)
                                                                        .setFromUserId(getUserId())
                                                                        .setTimestamp(System.currentTimeMillis()));
                                                                break;
                                                            case NetworkConstant.Method.DELETE:
                                                                chatRoomItem.setRequestId(null);
                                                                chatRoomItem.setStatus(null);
                                                                chattingFragment.addChatting(new ChattingItem()
                                                                        .setStatus(ChattingItem.MessageType.cancel)
                                                                        .setMsgType(CommonConst.Chatting.SYSTEM)
                                                                        .setFromUserId(getUserId())
                                                                        .setTimestamp(System.currentTimeMillis()));
                                                                break;
                                                            case NetworkConstant.Method.POST:
                                                            default:
                                                                chatRoomItem.setRequestId(getUserId());
                                                                chatRoomItem.setStatus(CommonConst.Chatting.MEETING_REQUEST);
                                                                chattingFragment.addChatting(new ChattingItem()
                                                                        .setStatus(ChattingItem.MessageType.request)
                                                                        .setMsgType(CommonConst.Chatting.SYSTEM)
                                                                        .setFromUserId(getUserId())
                                                                        .setTimestamp(System.currentTimeMillis()));
                                                                break;
                                                        }
                                                        setMeetingButton();
                                                    } else {
                                                        showErrorMessage();
                                                    }
                                                }
                                            });
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
            });
        }
    }

    private void startChatRoomListFragment() {
        if(roomListFragment == null) roomListFragment = new ChatRoomListFragment();
        fragmentManager.beginTransaction().replace(R.id.chat_container, roomListFragment, CommonConst.CHAT_LIST_FRAG).commit();
//        fragmentManager.executePendingTransactions();
    }

    private void startChattingFragment() {
        chattingFragment = ChattingFragment.newInstance(chatPref.getBoolean(SsomPreferences.PREF_CHATTING_GUIDE_IS_READ, false));
        chattingFragment.setChatRoomItem(chatRoomItem);
        fragmentManager.beginTransaction().replace(R.id.chat_container, chattingFragment, CommonConst.CHATTING_FRAG).commit();
    }

    @Override
    public void onChatItemClick(final int position) {
        CURRENT_STATE = STATE_CHAT_ROOM;
        chatRoomItem = roomListFragment.getChatRoomList().get(position);
        startChattingFragment();
        changeSsomBarViewForChattingRoom();
    }

    @Override
    public void onBackPressed() {
        if(imm.hideSoftInputFromWindow(null, 0)) {
            return;
        }

        if(CURRENT_STATE == STATE_CHAT_ROOM) {
            if(chatRoomItem != null && CommonConst.Chatting.MEETING_APPROVE.equals(chatRoomItem.getStatus())) {
                hideSoftKeyboard();
                UiUtils.makeCommonDialog(SsomChattingActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                        R.string.dialog_notice, R.style.ssom_font_20_red_pink_bold, R.string.dialog_meet_finish_by_exit_message,
                        0, R.string.dialog_meet_finish, R.string.dialog_close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                APICaller.sendChattingRequest(getToken(), chatRoomItem.getId(), NetworkConstant.Method.DELETE,
                                        new NetworkManager.NetworkListener<SsomResponse<SsomMeetingRequest.Response>>() {
                                            @Override
                                            public void onResponse(SsomResponse<SsomMeetingRequest.Response> response) {
                                                if(response.isSuccess()) {
                                                    chatRoomItem.setStatus(null);
                                                    onBackPressed();
                                                } else {
                                                    showErrorMessage();
                                                }
                                            }
                                        });
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                return;
            }

            CURRENT_STATE = STATE_CHAT_LIST;
            hideSoftKeyboard();
            if(unreadCount > 0 && chatRoomItem != null && chatRoomItem.getUnreadCount() != 0) unreadCount -= chatRoomItem.getUnreadCount();
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

        if(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH.equalsIgnoreCase(intent.getAction())) {

            if (fragmentManager.findFragmentById(R.id.chat_container) instanceof ChatRoomListFragment) {
                // chatting room 화면을 보고 있는 경우에 채팅룸 갱신
                ArrayList<ChatRoomItem> roomList = roomListFragment.getChatRoomList();
                int chatRoomId = Integer.parseInt(intent.getStringExtra(CommonConst.Intent.CHAT_ROOM_ID));
                int roomIndex = 0;

                for (int i = 0; i < roomList.size(); i++) {
                    if (roomList.get(i).getId() == chatRoomId) {
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
                ssomBar.setSsomBarTitleText(String.format(getString(R.string.chat_list_title), ++unreadCount));
            } else {
                // 푸시를 받은 방에서 chatting 중이라면 메시지를 추가해서 보여줌
                if (chattingFragment.getChatRoomId().equalsIgnoreCase(intent.getStringExtra(CommonConst.Intent.CHAT_ROOM_ID))) {
                    ChattingItem chat = new ChattingItem();
                    chat.setMsg(intent.getStringExtra(CommonConst.Intent.MESSAGE));
                    chat.setFromUserId(intent.getStringExtra(CommonConst.Intent.FROM_USER_ID));
                    chat.setToUserId(intent.getStringExtra(CommonConst.Intent.TO_USER_ID));
                    chat.setTimestamp(intent.getLongExtra(CommonConst.Intent.TIMESTAMP, 0));
                    if (!TextUtils.isEmpty(intent.getStringExtra(CommonConst.Intent.STATUS))) {
                        String status = intent.getStringExtra(CommonConst.Intent.STATUS);
                        ChattingItem.MessageType messageType;
                        switch (status) {
                            case CommonConst.Chatting.MEETING_REQUEST:
                                messageType = ChattingItem.MessageType.request;
                                break;
                            case CommonConst.Chatting.MEETING_APPROVE:
                                messageType = ChattingItem.MessageType.approve;
                                break;
                            case CommonConst.Chatting.MEETING_CANCEL:
                                messageType = ChattingItem.MessageType.cancel;
                                break;
                            case CommonConst.Chatting.MEETING_COMPLETE:
                                messageType = ChattingItem.MessageType.complete;
                                break;
                            case CommonConst.Chatting.MEETING_OUT:
                                messageType = ChattingItem.MessageType.finish;
                                break;
                            default:
                                messageType = null;
                                break;
                        }

                        chat.setStatus(messageType);
                        chat.setMsgType(CommonConst.Chatting.SYSTEM);
                        chatRoomItem.setRequestId(chat.getFromUserId());
                        chatRoomItem.setStatus(status);
                        chattingFragment.setChatRoomItem(chatRoomItem);
                        setMeetingButton();
                    } else {
                        chat.setMsgType(CommonConst.Chatting.NORMAL);
                    }
                    chattingFragment.addChatting(chat);
                } else {
                    unreadCount++;
                }
            }
        }
    }
}
