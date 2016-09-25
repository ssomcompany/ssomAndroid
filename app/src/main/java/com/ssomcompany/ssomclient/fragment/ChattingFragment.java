package com.ssomcompany.ssomclient.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.activity.SsomChattingGuideActivity;
import com.ssomcompany.ssomclient.adapter.ChattingAdapter;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetChattingList;
import com.ssomcompany.ssomclient.network.api.SendChattingMessage;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;

public class ChattingFragment extends BaseFragment {
    private static final String TAG = ChattingFragment.class.getSimpleName();

    private static final String IS_READ = "IS_READ";

    /**
     * The information layout and instance
     */
    private LinearLayout infoLayout;
    private TextView infoText;
    private TextView infoBtn;
    private EditText editMessage;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChattingAdapter mAdapter;
    private ArrayList<ChattingItem> chatList = new ArrayList<>();

    private ChatRoomItem roomItem;

    // send btn 제어
    boolean isSending = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChattingFragment() {
        super();
    }

    public static ChattingFragment newInstance(boolean isReadGuide) {
        ChattingFragment chattingFragment = new ChattingFragment();
        Bundle bundle= new Bundle();
        bundle.putBoolean(IS_READ, isReadGuide);
        chattingFragment.setArguments(bundle);
        return chattingFragment;
    }

    public void setChatRoomItem(ChatRoomItem roomItem) {
        this.roomItem = roomItem;
    }

    public String getChatRoomId() {
        return String.valueOf(roomItem.getId());
    }

    public String getChatRoomUserId() {
        return roomItem.getUserId();
    }

    public void addChatting(ChattingItem chat) {
        mAdapter.add(chat);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && !getArguments().getBoolean(IS_READ, false)) {
            Intent intent = new Intent(getActivity(), SsomChattingGuideActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        mAdapter = new ChattingAdapter(getActivity(), roomItem);
        mAdapter.setItemList(chatList);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        ImageView btnSend = (ImageView) view.findViewById(R.id.btn_send);
        editMessage = (EditText) view.findViewById(R.id.edit_message);
        ListView chatListView = (ListView) view.findViewById(R.id.chatting);
        infoLayout = (LinearLayout) view.findViewById(R.id.info_layout);
        infoText = (TextView) view.findViewById(R.id.info_text);
        infoBtn = (TextView) view.findViewById(R.id.info_btn);

        setChatRoomInfoLayout(roomItem.getInfoType());

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isSending) {
                    return;
                }

                // TODO message 없는 경우 있는 경우 send 버튼의 이미지가 다르게 설정 할지 문의
                if(TextUtils.isEmpty(editMessage.getText())) return;

                isSending = true;
                APICaller.sendChattingMessage(getToken(), roomItem.getId(), System.currentTimeMillis(), String.valueOf(editMessage.getText()),
                        new NetworkManager.NetworkListener<SsomResponse<SendChattingMessage.Response>>() {
                            @Override
                            public void onResponse(SsomResponse<SendChattingMessage.Response> response) {
                                if(response.isSuccess()) {
                                    Log.d(TAG, "response : " + response);
                                    if(response.getData() != null) {
                                        Log.d(TAG, "sent a message successfully.");
                                        for(ChattingItem item : response.getData().getChattingList()) {
                                            mAdapter.add(item);
                                        }
                                        mAdapter.add(String.valueOf(editMessage.getText()));
                                        editMessage.setText("");
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e(TAG, "unexpected error, data is null");
                                    }
                                } else {
                                    Log.e(TAG, "Response error with code " + response.getResultCode() +
                                            ", message : " + response.getMessage(), response.getError());
                                    showErrorMessage();
                                }
                                isSending = false;
                            }
                        });
            }
        });

        chatListView.setAdapter(mAdapter);
        editMessage.setSelected(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showProgressDialog();
        APICaller.getChattingList(getToken(), roomItem.getId(),
                new NetworkManager.NetworkListener<SsomResponse<GetChattingList.Response>>() {
                    @Override
                    public void onResponse(SsomResponse<GetChattingList.Response> response) {
                        if(response.isSuccess()) {
                            Log.d(TAG, "response : " + response);
                            if(response.getData() != null) {
                                chatList = response.getData().getChattingList();
                                chatList.add(0, new ChattingItem().setType(ChattingItem.MessageType.initial));
                                mAdapter.setItemList(chatList);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "unexpected error, data is null");
                            }
                        } else {
                            Log.e(TAG, "Response error with code " + response.getResultCode() +
                                    ", message : " + response.getMessage(), response.getError());
                            showErrorMessage();
                        }
                        dismissProgressDialog();
                    }
                });
    }

    public void setChatRoomInfoLayout(final ChatRoomItem.InfoType type) {
        if(type == null || type == ChatRoomItem.InfoType.none) {
            infoLayout.setVisibility(View.GONE);
            return;
        }

        infoLayout.setVisibility(View.VISIBLE);

        if(type == ChatRoomItem.InfoType.sent) {
            infoText.setText(getString(R.string.chat_room_info_text_sent));
            infoBtn.setText(getString(R.string.chat_room_info_btn_sent));
            infoBtn.setBackgroundResource(R.drawable.btn_chat_info_cancel);
        } else if(type == ChatRoomItem.InfoType.received) {
            infoText.setText(getString(R.string.chat_room_info_text_received));
            infoBtn.setText(getString(R.string.chat_room_info_btn_received));
            infoBtn.setBackgroundResource(R.drawable.btn_write_apply_ssoa);
        } else if(type == ChatRoomItem.InfoType.success) {
            infoText.setText(getString(R.string.chat_room_info_text_success));
            infoBtn.setText(getString(R.string.chat_room_info_btn_success));
            infoBtn.setBackgroundResource(R.drawable.btn_write_apply_ssoa);
            infoBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_heart_in_white_balloon, 0, 0, 0);
            infoBtn.setCompoundDrawablePadding(-(Util.convertDpToPixel(15)));
            infoBtn.setPadding(Util.convertDpToPixel(16), 0, 0, 0);
        }

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == ChatRoomItem.InfoType.success){
                    // TODO 지도 화면으로 이동

                } else {
                    UiUtils.makeCommonDialog(getActivity(), CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                            type == ChatRoomItem.InfoType.sent ? R.string.dialog_meet_cancel : R.string.dialog_meet_request,
                            R.style.ssom_font_20_grayish_brown_bold,
                            type == ChatRoomItem.InfoType.sent ? R.string.dialog_meet_request_cancel_message : R.string.dialog_meet_received_message, 0,
                            type == ChatRoomItem.InfoType.sent ? R.string.ok_upper : R.string.dialog_meet,
                            type == ChatRoomItem.InfoType.sent ? R.string.dialog_cancel : R.string.dialog_sorry,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO call accept api (만남 승인, 요청취소)

                                    mAdapter.notifyDataSetChanged();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO call reject api (만남 거절, 요청 안 취소)
                                }
                            });
                }
            }
        });
    }
}
