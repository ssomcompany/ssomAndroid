package com.ssomcompany.ssomclient.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.adapter.ChattingAdapter;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;

public class ChattingFragment extends BaseFragment {
    private static final String TAG = ChattingFragment.class.getSimpleName();

    /**
     * The fragment's ListView/GridView.
     */
    private ListView chatListView;
    private EditText editMessage;
    private ImageView btnSend;

    /**
     * The information layout and instance
     */
    private LinearLayout infoLayout;
    private TextView infoText;
    private TextView infoBtn;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChattingAdapter mAdapter;
    private ArrayList<ChattingItem> chatList;

    private ChatRoomItem roomItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChattingFragment() {
        super();
    }

    public void setChatRoomItem(ChatRoomItem roomItem) {
        this.roomItem = roomItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO chatting 내역 조회 후 response 에서 list setting
        if(chatList == null || chatList.isEmpty()) {
            chatList = new ArrayList<>();
            ChattingItem initial;
            for(int i = 0 ; i < 4 ; i++) {
                initial = new ChattingItem();
                initial.setPostId(roomItem.getPostId());
                if(i == 0) {
                    initial.setType(ChattingItem.MessageType.initial);
                } else {
                    initial.setType(ChattingItem.MessageType.receive);
                    initial.setMessageTime(System.currentTimeMillis());
                    initial.setMessage("test message " + i);
                }
                chatList.add(initial);
            }
        }
        mAdapter = new ChattingAdapter(getActivity(), roomItem, chatList);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        editMessage = (EditText) view.findViewById(R.id.edit_message);
        btnSend = (ImageView) view.findViewById(R.id.btn_send);
        chatListView = (ListView) view.findViewById(R.id.chatting);
        infoLayout = (LinearLayout) view.findViewById(R.id.info_layout);
        infoText = (TextView) view.findViewById(R.id.info_text);
        infoBtn = (TextView) view.findViewById(R.id.info_btn);

        setChatRoomInfoLayout(roomItem.getInfoType());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(editMessage.getText().toString())) return;

                mAdapter.add(editMessage.getText().toString());
                editMessage.setText("");
                // TODO send message network api call & 메시지 추가
            }
        });

        chatListView.setAdapter(mAdapter);

        return view;
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
                    makeCommonDialog(type);
                }
            }
        });
    }

    private void makeCommonDialog(ChatRoomItem.InfoType type) {
        CommonDialog dialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_ALERT_BUTTON);
        dialog.setTitle(getString(R.string.dialog_meet_request));
        dialog.setTitleStyle(R.style.ssom_font_20_grayish_brown_bold);
        dialog.setMessage(getString(type == ChatRoomItem.InfoType.sent ? R.string.dialog_meet_request_message : R.string.dialog_meet_received_message));
        dialog.setPositiveButton(getString(R.string.dialog_meet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO call accept api (만남 승인, 요청취소)

                mAdapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton(getString(type == ChatRoomItem.InfoType.sent ? R.string.dialog_cancel : R.string.dialog_sorry),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO call reject api (만남 거절, 요청 안 취소)
            }
        });
        dialog.setAutoDismissEnable(true);
        dialog.show(getActivity().getFragmentManager(), null);
    }
}
