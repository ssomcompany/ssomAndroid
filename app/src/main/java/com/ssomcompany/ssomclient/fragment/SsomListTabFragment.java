package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.adapter.SsomItemListAdapter;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ViewListener.OnPostItemInteractionListener}
 * interface.
 */
public class SsomListTabFragment extends RetainedStateFragment implements AbsListView.OnItemClickListener {
    private static final String TAG = SsomListTabFragment.class.getSimpleName();
    private static final String SSOM_LIST = "ssomList";

    private ViewListener.OnPostItemInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SsomItemListAdapter mAdapter;
    private ArrayList<SsomItem> ssomList;

    public SsomListTabFragment setSsomListData(ArrayList<SsomItem> ssomList) {
        this.ssomList = ssomList;
        return this;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SsomListTabFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new SsomItemListAdapter(getActivity());
        mAdapter.setItemList(ssomList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState == null) outState = new Bundle();
        outState.putSerializable(SSOM_LIST, ssomList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(SSOM_LIST)) {
            ssomList = (ArrayList<SsomItem>) savedInstanceState.getSerializable(SSOM_LIST);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ssom_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        TextView emptyView = new TextView(getContext());
        emptyView.setTextAppearance(getContext(), R.style.ssom_font_14_pinkish_gray_two);
        emptyView.setMaxLines(2);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setCompoundDrawablePadding(Util.convertDpToPixel(16f));
        emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_couple_gray, 0, 0);
        mListView.setEmptyView(emptyView);
        setEmptyText(getString(R.string.empty_ssom_list));

        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mListener = (ViewListener.OnPostItemInteractionListener) activity;
                ((MainActivity) activity).setOnTabChangedListener(mTabListener);
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
            mListener = (ViewListener.OnPostItemInteractionListener) context;
            ((MainActivity) context).setOnTabChangedListener(mTabListener);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPostItemClick(ssomList, position);
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

    public void ssomListNotifyDataSetChanged() {
        mAdapter.setItemList(ssomList);
        mAdapter.notifyDataSetChanged();
    }

    private ViewListener.OnTabChangedListener mTabListener = new ViewListener.OnTabChangedListener() {
        @Override
        public void onTabChangedAction(ArrayList<SsomItem> ssomList) {
            Log.d(TAG, "onTabChangedAction() called !!");
            setSsomListData(ssomList);
            ssomListNotifyDataSetChanged();
            mListener = (ViewListener.OnPostItemInteractionListener) getActivity();
            ((MainActivity) getActivity()).dismissProgressDialog();
        }
    };
}
