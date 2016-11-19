package com.ssomcompany.ssomclient.fragment;

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
import android.widget.ListView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.SsomChattingActivity;
import com.ssomcompany.ssomclient.activity.SsomChattingGuideActivity;
import com.ssomcompany.ssomclient.activity.SsomMapActivity;
import com.ssomcompany.ssomclient.adapter.ChattingAdapter;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetChattingList;
import com.ssomcompany.ssomclient.network.api.SendChattingMessage;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

import java.util.ArrayList;

public class ChattingFragment extends BaseFragment {
    private static final String TAG = ChattingFragment.class.getSimpleName();

    private static final String IS_READ = "IS_READ";

    private EditText editMessage;
    private View mapLayout;

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
        checkEditEnable();
    }

    private void checkEditEnable() {
        if(CommonConst.Chatting.MEETING_OUT.equalsIgnoreCase(roomItem.getStatus())) {
            if(editMessage != null) editMessage.setEnabled(false);
        }
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
    public void onStart() {
        super.onStart();

        showProgressDialog();
        APICaller.getChattingList(getToken(), roomItem.getId(),
                new NetworkManager.NetworkListener<SsomResponse<GetChattingList.Response>>() {
                    @Override
                    public void onResponse(SsomResponse<GetChattingList.Response> response) {
                        if(response.isSuccess()) {
                            Log.d(TAG, "response : " + response);
                            if(response.getData() != null) {
                                chatList = response.getData().getChattingList();
                                chatList.add(0, new ChattingItem().setStatus(ChattingItem.MessageType.initial)
                                        .setMsgType(CommonConst.Chatting.SYSTEM).setTimestamp(roomItem.getCreatedTimestamp()));
                                mAdapter.setItemList(chatList);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "unexpected error, data is null");
                                showErrorMessage();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        ImageView btnSend = (ImageView) view.findViewById(R.id.btn_send);
        editMessage = (EditText) view.findViewById(R.id.edit_message);
        ListView chatListView = (ListView) view.findViewById(R.id.chatting);
        mapLayout = view.findViewById(R.id.ssom_map_layout);

        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity(), SsomMapActivity.class);
                mapIntent.putExtra(CommonConst.Intent.EXTRA_ROOM_ITEM, roomItem);
                startActivity(mapIntent);
            }
        });

        // 채팅룸 우측 상단의 버튼을 기능에 맞게 적용
        ((SsomChattingActivity) getActivity()).setMeetingButton();

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isSending) {
                    return;
                }

                if(CommonConst.Chatting.MEETING_OUT.equalsIgnoreCase(roomItem.getStatus())) {
                    UiUtils.makeToastMessage(getActivity(), "상대방이 방에서 나갔습니다 T.T");
                    return;
                }

                // TODO message 없는 경우 있는 경우 send 버튼의 이미지가 다르게 설정 할지 문의
                if(TextUtils.isEmpty(editMessage.getText())) return;

                isSending = true;
                // 내가 보고 있는 목록의 가장 마지막 메시지의 시간을 가져와서 보내주면 그 사이 받은 메시지를 return 해줌
                APICaller.sendChattingMessage(getToken(), roomItem.getId(),
                        mAdapter.getItemList().get(mAdapter.getItemList().size() - 1).getTimestamp(), String.valueOf(editMessage.getText()),
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
                                        showErrorMessage();
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
        View dummy = new View(getActivity());
        dummy.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.convertDpToPixel(7f)));
        chatListView.addFooterView(dummy);
        editMessage.setSelected(true);
        checkEditEnable();

        return view;
    }

    public void enableMapLayout(boolean enabled) {
        mapLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
