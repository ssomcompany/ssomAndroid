package com.ssomcompany.ssomclient.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.adapter.SsomChattingAdapter;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;

import java.util.ArrayList;

public class SsomChattingActivity extends BaseActivity implements AbsListView.OnItemClickListener {
    private static final String TAG = SsomChattingActivity.class.getSimpleName();
    public static final String EXTRA_KEY_CHAT_LIST = "chatList";

    private SsomChattingAdapter mAdapter;
    private ArrayList<ChattingItem> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        chatList = (ArrayList<ChattingItem>) getIntent().getSerializableExtra(EXTRA_KEY_CHAT_LIST);

        Log.d(TAG, "chatList size : " + chatList.size());
        mAdapter = new SsomChattingAdapter(this);
        mAdapter.setItemList(chatList);

        SsomActionBarView ssomBar = (SsomActionBarView) findViewById(R.id.ssom_action_bar);
        ssomBar.setCurrentMode(SsomActionBarView.SSOM_CHAT_LIST);
        ssomBar.setHeartLayoutVisibility(false);
        ssomBar.setSsomBarTitleVisibility(true);
        ssomBar.setSsomBarTitleGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomBar.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        ssomBar.setSsomBarTitle(String.format(getResources().getString(R.string.chat_list_title), 0));
        // TODO MessageManager 등록하고 초기 message count 셋팅
        ssomBar.setChatIconOnOff(false);
        ssomBar.setChatCount("0");

        AbsListView listChatting = (ListView) findViewById(R.id.list_chatting);
        listChatting.setAdapter(mAdapter);
        listChatting.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void putChattingToList(ChattingItem item) {
        chatList.add(0, item);
        mAdapter.setItemList(chatList);
        mAdapter.notifyDataSetChanged();
    }
}
