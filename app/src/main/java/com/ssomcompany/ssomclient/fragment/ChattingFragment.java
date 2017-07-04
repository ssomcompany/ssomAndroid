package com.ssomcompany.ssomclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.ssomcompany.ssomclient.network.RetrofitManager;
import com.ssomcompany.ssomclient.network.api.ChatService;
import com.ssomcompany.ssomclient.network.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.ChattingItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    String sendingMsg;

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
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
        RetrofitManager.getInstance().create(ChatService.class)
                .requestChatList(roomItem.getId())
                .enqueue(new Callback<ArrayList<ChattingItem>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ChattingItem>> call, Response<ArrayList<ChattingItem>> response) {
                        if(response.isSuccessful()) {
                            Log.d(TAG, "response : " + response);
                            if(response.body() != null) {
                                chatList = response.body();
                                chatList.add(0, new ChattingItem().setStatus(ChattingItem.MessageType.initial)
                                        .setMsgType(CommonConst.Chatting.SYSTEM).setTimestamp(roomItem.getCreatedTimestamp()));
                                mAdapter.setItemList(chatList);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "unexpected error, data is null");
                                showErrorMessage();
                            }
                        } else {
                            Log.e(TAG, "Response error with code " + response.code() + ", message : " + response.message());
                            showErrorMessage();
                        }
                        dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ChattingItem>> call, Throwable t) {

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

        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isSending && s.length() != sendingMsg.length()) {
                    editMessage.setText(sendingMsg);
                    editMessage.setSelection(sendingMsg.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 채팅룸 우측 상단의 버튼을 기능에 맞게 적용
        ((SsomChattingActivity) getActivity()).setMeetingButton();

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO message 없는 경우 있는 경우 send 버튼의 이미지가 다르게 설정 할지 문의
                // 빈 메시지 동작 없음
                if(TextUtils.isEmpty(editMessage.getText())) return;
                // 현재 메시지를 보내는 중이면 다시 보내지 않음
                if(isSending) {
                    return;
                }

                if(CommonConst.Chatting.MEETING_OUT.equalsIgnoreCase(roomItem.getStatus())) {
                    UiUtils.makeToastMessage(getActivity(), "상대방이 방에서 나갔습니다 T.T");
                    return;
                }

                isSending = true;
                sendingMsg = String.valueOf(editMessage.getText());
                // 내가 보고 있는 목록의 가장 마지막 메시지의 시간을 가져와서 보내주면 그 사이 받은 메시지를 return 해줌
                RetrofitManager.getInstance().create(ChatService.class)
                        .sendChatMessage(roomItem.getId(), mAdapter.getItemList().get(mAdapter.getItemList().size() - 1).getTimestamp(), sendingMsg)
                        .enqueue(new Callback<ArrayList<ChattingItem>>() {
                            @Override
                            public void onResponse(Call<ArrayList<ChattingItem>> call, Response<ArrayList<ChattingItem>> response) {
                                isSending = false;
                                if(response.isSuccessful()) {
                                    Log.d(TAG, "response : " + response);
                                    if(response.body() != null) {
                                        Log.d(TAG, "sent a message successfully.");
                                        for(ChattingItem item : response.body()) {
                                            mAdapter.add(item);
                                        }
                                        mAdapter.add(sendingMsg);
                                        editMessage.setText("");
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e(TAG, "unexpected error, data is null");
                                        showErrorMessage();
                                    }
                                } else {
                                    Log.e(TAG, "Response error with code " + response.code() + ", message : " + response.message());
                                    showErrorMessage();
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<ChattingItem>> call, Throwable t) {

                            }
                        });
            }
        });

        chatListView.setAdapter(mAdapter);
        View dummy = new View(getActivity());
        dummy.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Util.convertDpToPixel(7f)));
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
