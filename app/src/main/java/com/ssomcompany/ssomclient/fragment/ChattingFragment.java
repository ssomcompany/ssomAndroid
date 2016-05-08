package com.ssomcompany.ssomclient.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.adapter.ChattingAdapter;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;

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
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChattingAdapter mAdapter;
    private ArrayList<ChattingItem> chatList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChattingFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO chatting 내역 조회 후 response 에서 list setting
        if(chatList == null || chatList.isEmpty()) {
            chatList = new ArrayList<>();
            ChattingItem initial = new ChattingItem();
            initial.setType(ChattingItem.MessageType.initial);
            chatList.add(initial);
        }
        mAdapter = new ChattingAdapter(getActivity(), chatList);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(editMessage.getText().toString())) return;

                mAdapter.add(editMessage.getText().toString());
                editMessage.setText("");
                // TODO send message network api call & 메시지 추가
            }
        });

        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO long click 삭제기능 추가 시 api call
                return false;
            }
        });
        chatListView.setAdapter(mAdapter);

        return view;
    }
}
