package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.adapter.ChatRoomListAdapter;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.DeleteChattingRoom;
import com.ssomcompany.ssomclient.network.api.GetChattingRoomList;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenu;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenuCreator;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenuItem;
import com.ssomcompany.ssomclient.widget.swipelistview.SwipeMenuListView;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ViewListener.OnChatItemInteractionListener}
 * interface.
 */
public class ChatRoomListFragment extends BaseFragment {
    private static final String TAG = ChatRoomListFragment.class.getSimpleName();

    private ViewListener.OnChatItemInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private SwipeMenuListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatRoomListAdapter mAdapter;
    private ArrayList<ChatRoomItem> chatRoomList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        mListView = (SwipeMenuListView) view.findViewById(R.id.list_chatting);
        mListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
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
                                    APICaller.deleteChattingRoom(getToken(), chatRoomList.get(position).getId(),
                                            new NetworkManager.NetworkListener<SsomResponse<DeleteChattingRoom.Response>>() {
                                                @Override
                                                public void onResponse(SsomResponse<DeleteChattingRoom.Response> response) {
                                                    if(response.isSuccess()) {
                                                        chatRoomList.remove(position);
                                                        mAdapter.notifyDataSetChanged();
                                                    } else {
                                                        showErrorMessage();
                                                    }
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
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onChatItemClick(position);
                }
            }
        });
        mListView.setCloseInterpolator(new BounceInterpolator());
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showProgressDialog();
        APICaller.getChattingRoomList(getToken(), new NetworkManager.NetworkListener<SsomResponse<GetChattingRoomList.Response>>() {
            @Override
            public void onResponse(SsomResponse<GetChattingRoomList.Response> response) {
                if(response.isSuccess()) {
                    if(response.getData() != null) {
                        chatRoomList = response.getData().getChattingRoomList();
                        mAdapter.setChatRoomList(chatRoomList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "unexpected error, data is null");
                        showErrorMessage();
                    }
                } else {
                    showErrorMessage();
                }
                dismissProgressDialog();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mListener = (ViewListener.OnChatItemInteractionListener) activity;
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
            mListener = (ViewListener.OnChatItemInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPostItemInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
