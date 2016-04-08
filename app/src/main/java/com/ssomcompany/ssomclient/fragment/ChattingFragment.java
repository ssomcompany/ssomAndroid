package com.ssomcompany.ssomclient.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.adapter.ChatItemListAdapter;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;

public class ChattingFragment extends BaseFragment {
    private static final String TAG = ChattingFragment.class.getSimpleName();

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatItemListAdapter mAdapter;
    private ArrayList<ChattingItem> chatList;

    private static ChattingFragment chattingFragment;

    public static ChattingFragment newInstance() {
        if(chattingFragment == null) {
            chattingFragment = new ChattingFragment();
        }
        return chattingFragment;
    }

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

        mAdapter = new ChatItemListAdapter(getActivity(), chatList);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        return view;
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
