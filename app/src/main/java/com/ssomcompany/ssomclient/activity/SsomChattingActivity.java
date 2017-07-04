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
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.network.RetrofitManager;
import com.ssomcompany.ssomclient.network.api.ChatService;
import com.ssomcompany.ssomclient.network.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.ChattingItem;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.MessageManager;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SsomChattingActivity extends BaseActivity implements MessageCountCheck {
    private static final String TAG = SsomChattingActivity.class.getSimpleName();

    // fragment instance 저장
    private ChatRoomItem chatRoomItem;
    private ChattingFragment chattingFragment;

    private FragmentManager fragmentManager;
    private SsomActionBarView ssomBar;

    private SsomPreferences chatPref;
    private InputMethodManager imm;

    // network
    private ChatService service;

    private enum REQUEST_STATUS {
        REQUEST, APPROVE, DELETE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fragmentManager = getSupportFragmentManager();
        chatPref = new SsomPreferences(this, SsomPreferences.CHATTING_PREF);
        ssomBar = (SsomActionBarView) findViewById(R.id.ssom_toolbar);

        if(getIntent() != null && getIntent().getExtras() != null) {
            chatRoomItem = (ChatRoomItem) getIntent().getSerializableExtra(CommonConst.Intent.CHAT_ROOM_ITEM);
        } else {
            finish();
            return;
        }

        service = RetrofitManager.getInstance().create(ChatService.class);
        initSsomBarView();
        startChattingFragment();
    }

    private void initSsomBarView() {
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHATTING);
        ssomBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarTitleText(getString(R.string.chat_room_title));
        ssomBar.setSsomBarSubTitleVisibility(true);
        ssomBar.setSsomBarSubTitleText((chatRoomItem.getMinAge() | chatRoomItem.getUserCount()) == 0 ? getString(R.string.chat_room_no_info) :
                String.format(getString(R.string.filter_age_n_count), Util.convertAgeRange(chatRoomItem.getMinAge()), Util.convertPeopleRange(chatRoomItem.getUserCount())));
        ssomBar.setSsomBarSubTitleStyle(R.style.ssom_font_12_pinkish_gray_two_single);
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

    public void setMeetingButton() {
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
            final REQUEST_STATUS status;

            if (CommonConst.Chatting.MEETING_REQUEST.equals(chatRoomItem.getStatus())) {
                boolean isMyRequest = getUserId().equals(chatRoomItem.getRequestId());
                // 내가 요청한거면 요청취소, 내가 요청한게 아니면 만남수락
                ssomBar.setChattingRoomBtnMeetingOnOff(!isMyRequest);
                ssomBar.setChattingRoomBtnMeetingTitle(isMyRequest ?
                        getString(R.string.chat_room_info_btn_sent) : getString(R.string.dialog_meet_apply));
                chattingFragment.enableMapLayout(false);
                status = isMyRequest ?  REQUEST_STATUS.DELETE : REQUEST_STATUS.APPROVE;
                dialogMsg = isMyRequest ? R.string.dialog_meet_request_cancel_message : R.string.dialog_meet_received_message;
                dialogOkBtn = isMyRequest ? R.string.chat_room_info_btn_sent : R.string.dialog_meet_apply;
                dialogNoBtn = isMyRequest ? R.string.dialog_close : R.string.dialog_not_now;
            } else if(CommonConst.Chatting.MEETING_APPROVE.equals(chatRoomItem.getStatus())) {
                // 만남 중이니 서로 만남취소 가능
                ssomBar.setChattingRoomBtnMeetingOnOff(false);
                ssomBar.setChattingRoomBtnMeetingTitle(getString(R.string.dialog_meet_finish));
                chattingFragment.enableMapLayout(true);
                status = REQUEST_STATUS.DELETE;
                dialogMsg = R.string.dialog_meet_finish_message;
                dialogOkBtn = R.string.dialog_meet_finish;
                dialogNoBtn = R.string.dialog_close;
            } else {
                ssomBar.setChattingRoomBtnMeetingOnOff(true);
                ssomBar.setChattingRoomBtnMeetingTitle(getString(R.string.dialog_meet_request));
                chattingFragment.enableMapLayout(false);
                status = REQUEST_STATUS.REQUEST;
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
                                    requestMethod(status).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if(response.isSuccessful()) {
                                                switch (status) {
                                                    case APPROVE:
                                                        chatRoomItem.setRequestId(getUserId());
                                                        chatRoomItem.setStatus(CommonConst.Chatting.MEETING_APPROVE);
                                                        chattingFragment.addChatting(new ChattingItem()
                                                                .setStatus(ChattingItem.MessageType.approve)
                                                                .setMsgType(CommonConst.Chatting.SYSTEM)
                                                                .setFromUserId(getUserId())
                                                                .setTimestamp(System.currentTimeMillis()));
                                                        break;
                                                    case DELETE:
                                                        chatRoomItem.setRequestId(null);
                                                        chatRoomItem.setStatus(null);
                                                        chattingFragment.addChatting(new ChattingItem()
                                                                .setStatus(ChattingItem.MessageType.cancel)
                                                                .setMsgType(CommonConst.Chatting.SYSTEM)
                                                                .setFromUserId(getUserId())
                                                                .setTimestamp(System.currentTimeMillis()));
                                                        break;
                                                    case REQUEST:
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

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

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

    private Call<Void> requestMethod(REQUEST_STATUS status) {
        Call<Void> call;
        switch (status) {
            case APPROVE:
                call = service.approveMeeting(chatRoomItem.getId());
                break;
            case DELETE:
                call = service.cancelMeeting(chatRoomItem.getId());
                break;
            case REQUEST:
            default:
                call = service.requestMeeting(chatRoomItem.getId());
                break;
        }
        return call;
    }

    private void startChattingFragment() {
        chattingFragment = ChattingFragment.newInstance(chatPref.getBoolean(SsomPreferences.PREF_CHATTING_GUIDE_IS_READ, false));
        chattingFragment.setChatRoomItem(chatRoomItem);
        fragmentManager.beginTransaction().replace(R.id.chat_container, chattingFragment, CommonConst.CHATTING_FRAG).commit();
    }

    @Override
    public void onBackPressed() {
        if(imm.hideSoftInputFromWindow(null, 0)) {
            return;
        }

        hideSoftKeyboard();
        if(chatRoomItem != null && CommonConst.Chatting.MEETING_APPROVE.equals(chatRoomItem.getStatus())) {
            UiUtils.makeCommonDialog(SsomChattingActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                    R.string.dialog_notice, R.style.ssom_font_20_red_pink_bold, R.string.dialog_meet_finish_by_exit_message,
                    0, R.string.dialog_meet_finish, R.string.dialog_close,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            service.cancelMeeting(chatRoomItem.getId())
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if(response.isSuccessful()) {
                                                chatRoomItem.setStatus(null);
                                                onBackPressed();
                                            } else {
                                                showErrorMessage();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

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

        service.updateChatRoom(chatRoomItem.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Log.d(TAG, "success to update chatting time");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
        super.onBackPressed();
    }

    @Override
    protected void receivedPushMessage(Intent intent) {
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
                runVibrator();
            }
        }
    }
}
