package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.adapter.ChatItemListAdapter;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
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
 * Activities containing this fragment MUST implement the {@link OnChatItemInteractionListener}
 * interface.
 */
public class ChatListFragment extends BaseFragment {
    private static final String TAG = ChatListFragment.class.getSimpleName();

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnChatItemInteractionListener {
        void onChatItemClick(int position);
    }

    private OnChatItemInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private SwipeMenuListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatItemListAdapter mAdapter;
    private ArrayList<ChattingItem> chatList;

    private static ChatListFragment chatListFragment;

    public static ChatListFragment newInstance() {
        if(chatListFragment == null) {
            chatListFragment = new ChatListFragment();
        }
        return chatListFragment;
    }

    public void setChatListData(ArrayList<ChattingItem> chatList) {
        this.chatList = chatList;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ChatItemListAdapter(getActivity(), chatList);
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
                deleteItem.setTitle(getResources().getString(R.string.chat_list_delete));
                deleteItem.setTitleStyle(R.style.ssom_font_12_white_single);
                deleteItem.setMarginBetweenIconAndText(Util.convertDpToPixel(7.5f));
                menu.addMenuItem(deleteItem);
            }
        });
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    // delete chatting list item
                    makeCommonDialog(position);
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListView.isListItemOpened(position)) {
                    mListView.smoothCloseMenu(position);
                } else {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onChatItemClick(position);
                    }
                }
            }
        });
        mListView.setCloseInterpolator(new BounceInterpolator());
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnChatItemInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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

    private void makeCommonDialog(final int position) {
        CommonDialog dialog;
        if (getResources() == null) {
            Log.e(TAG, "getResources() is null!!!!");
            return;
        }

        dialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_ALERT_BUTTON);
        dialog.setTitle(getResources().getString(R.string.dialog_notice));
        dialog.setMessage(getResources().getString(R.string.dialog_chat_list_delete_message));
        dialog.setPositiveButton(getResources().getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO delete chatting api call

                chatList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        dialog.setAutoDissmissEnable(true);
        dialog.show(getActivity().getFragmentManager(), null);
    }
}
