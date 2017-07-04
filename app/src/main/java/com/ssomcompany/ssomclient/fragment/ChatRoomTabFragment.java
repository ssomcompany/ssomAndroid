package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.SsomChattingActivity;
import com.ssomcompany.ssomclient.adapter.ChatRoomListAdapter;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.RetrofitManager;
import com.ssomcompany.ssomclient.network.api.ChatService;
import com.ssomcompany.ssomclient.network.model.ChatRoomItem;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenu;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenuCreator;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenuItem;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenuListView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ViewListener.OnChatItemInteractionListener}
 * interface.
 */
public class ChatRoomTabFragment extends RetainedStateFragment {
    private static final String TAG = ChatRoomTabFragment.class.getSimpleName();

    /**
     * The fragment's ListView/GridView.
     */
    private SwipeMenuListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatRoomListAdapter mAdapter;
    private ArrayList<ChatRoomItem> chatRoomList = new ArrayList<>();

    private ViewListener.OnChatRoomListLoadingFinished mLoadingListener;

    public ArrayList<ChatRoomItem> getChatRoomList() {
        return chatRoomList;
    }

    public void setChatRoomListAndNotify(ArrayList<ChatRoomItem> chatRoomList) {
        this.chatRoomList = chatRoomList;
        mAdapter.setChatRoomList(this.chatRoomList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ChatRoomListAdapter(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        requestChatRoomList();
    }

    private void requestChatRoomList() {
        showProgressDialog();
        RetrofitManager.getInstance().create(ChatService.class)
                .requestChatRoomList()
                .enqueue(new Callback<ArrayList<ChatRoomItem>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ChatRoomItem>> call, Response<ArrayList<ChatRoomItem>> response) {
                        if(response.isSuccessful()) {
                            if(response.body() != null) {
                                chatRoomList = response.body();
                                mAdapter.setChatRoomList(chatRoomList);
                                mAdapter.notifyDataSetChanged();
                                // 채팅방으로 바로 보내기 위함
                                if(mLoadingListener != null) mLoadingListener.onFinishLoadingRoomList(chatRoomList);
                            } else {
                                Log.e(TAG, "unexpected error, data is null");
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                        dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ChatRoomItem>> call, Throwable t) {

                    }
                });
    }

    public void notifyChatRoomAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        mListView = (SwipeMenuListView) view.findViewById(R.id.list_chatting);
        mListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                deleteItem.setBackground(R.drawable.bg_chat_delete);
                deleteItem.setWidth(Util.convertDpToPixel(70));
                deleteItem.setIcon(R.drawable.icon_trash);
                deleteItem.setTitle(getString(R.string.chat_list_delete));
                deleteItem.setTitleStyle(R.style.ssom_font_12_white_single);
                deleteItem.setMarginBetweenIconAndText(Util.convertDpToPixel(7.5f));
                menu.addMenuItem(deleteItem);
            }
        });
        // opened menu click listener
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    // delete chatting list item
                    UiUtils.makeCommonDialog(getActivity(), CommonDialog.DIALOG_STYLE_ALERT_BUTTON,
                            R.string.dialog_notice, 0, R.string.dialog_chat_list_delete_message, 0,
                            R.string.dialog_finish, R.string.dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RetrofitManager.getInstance().create(ChatService.class)
                                            .deleteChatRoom(chatRoomList.get(position).getId())
                                            .enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if(response.isSuccessful()) {
                                                        chatRoomList.remove(position);
                                                        mAdapter.notifyDataSetChanged();
                                                    } else {
                                                        showErrorMessage();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {

                                                }
                                            });
                                }
                            }, null);
                }
                return false;
            }
        });
        // list item click listener
        mListView.setOnNormalItemClickListener(new SwipeMenuListView.OnNormalItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent chatIntent = new Intent(getActivity(), SsomChattingActivity.class);
                chatIntent.putExtra(CommonConst.Intent.CHAT_ROOM_ITEM, chatRoomList.get(position));
                startActivity(chatIntent);
            }
        });
        mListView.setCloseInterpolator(new BounceInterpolator());
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.emptyView));

        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mLoadingListener = (ViewListener.OnChatRoomListLoadingFinished) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnPostItemInteractionListener");
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mLoadingListener = (ViewListener.OnChatRoomListLoadingFinished) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPostItemInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public ChatRoomItem getChatRoomItem(String chatRoomId) {
        for(ChatRoomItem room : chatRoomList) {
            if(room.getId().equals(chatRoomId)) {
                return room;
            }
        }
        return null;
    }
}
