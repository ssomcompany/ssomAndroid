package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.adapter.SsomItemListAdapter;
import com.ssomcompany.ssomclient.common.CommonConst;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPostItemInteractionListener}
 * interface.
 */
public class SsomListFragment extends BaseFragment implements AbsListView.OnItemClickListener {
    private static final String TAG = SsomListFragment.class.getSimpleName();

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnPostItemInteractionListener {
        void onPostItemClick(String id);
    }

    private OnPostItemInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SsomItemListAdapter mAdapter;

    private static SsomListFragment ssomListFragment;

    public static SsomListFragment newInstance() {
        if(ssomListFragment == null) {
            ssomListFragment = new SsomListFragment();
        }
        return ssomListFragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SsomListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new SsomItemListAdapter(getActivity());
        mAdapter.setItemList(postItemList);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ssom, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPostItemInteractionListener) activity;
            if(activity instanceof MainActivity) ((MainActivity) activity).setOnTabChangedListener(mTabListener);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPostItemClick(postItemList.get(position).getPostId());
        }
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

    // TODO activity result
    public void onActivityResult() {

    }

    public void ssomListNotifyDataSetChanged() {
        mAdapter.setItemList(postItemList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    void setPostItems() {
        if(getActivity() instanceof MainActivity) {
            postItemList = ((MainActivity) getActivity()).getCurrentPostItems();
            postItemMap = ((MainActivity) getActivity()).getCurrentPostMap();
        }
    }

    private MainActivity.OnTabChangedListener mTabListener = new MainActivity.OnTabChangedListener() {
        @Override
        public void onTabChangedAction() {
            Log.d(TAG, "onTabChangedAction() called !!");
            setPostItems();
            ssomListNotifyDataSetChanged();
        }
    };
}
