package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.SsomPreferences;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFilterFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {
    private static final String TAG = "FilterFragment";

    // set preferences
    private static SsomPreferences filterPref;

    // set views
    private TextView tvTwentyEarly;
    private TextView tvTwentyMiddle;
    private TextView tvTwentyLate;
    private TextView tvThirtyAll;
    private TextView tvOnePeople;
    private TextView tvTwoPeople;
    private TextView tvThreePeople;
    private TextView tvFourPeopleOrMore;

    // set buttons
    private TextView tvCancel;
    private TextView tvApply;

    // set global filter params
    private static int age;
    private static int people;

    private OnFilterFragmentInteractionListener mListener;
    private static FilterFragment filterFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilterFragment.
     */
    public static FilterFragment newInstance() {
        if(filterFragment == null) {
            filterFragment = new FilterFragment();
        }

        return filterFragment;
    }

    public FilterFragment() { super(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if(getActivity() == null) {
            Log.e(TAG, "getActivity() is null.");
            return;
        }

        filterPref = new SsomPreferences(getActivity(), SsomPreferences.FILTER_PREF);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        view.setClickable(true);

        age = filterPref.getInt(SsomPreferences.PREF_FILTER_AGE, 20);
        people = filterPref.getInt(SsomPreferences.PREF_FILTER_PEOPLE, 1);

        // view for age settings
        tvTwentyEarly = (TextView) view.findViewById(R.id.tv_filter_age_20_early);
        tvTwentyMiddle = (TextView) view.findViewById(R.id.tv_filter_age_20_middle);
        tvTwentyLate = (TextView) view.findViewById(R.id.tv_filter_age_20_late);
        tvThirtyAll = (TextView) view.findViewById(R.id.tv_filter_age_30_all);

        // view for people settings
        tvOnePeople = (TextView) view.findViewById(R.id.tv_filter_people_1);
        tvTwoPeople = (TextView) view.findViewById(R.id.tv_filter_people_2);
        tvThreePeople = (TextView) view.findViewById(R.id.tv_filter_people_3);
        tvFourPeopleOrMore = (TextView) view.findViewById(R.id.tv_filter_people_4_n_over);

        tvTwentyEarly.setOnClickListener(filterAgeClickListener);
        tvTwentyMiddle.setOnClickListener(filterAgeClickListener);
        tvTwentyLate.setOnClickListener(filterAgeClickListener);
        tvThirtyAll.setOnClickListener(filterAgeClickListener);

        tvOnePeople.setOnClickListener(filterPeopleClickListener);
        tvTwoPeople.setOnClickListener(filterPeopleClickListener);
        tvThreePeople.setOnClickListener(filterPeopleClickListener);
        tvFourPeopleOrMore.setOnClickListener(filterPeopleClickListener);

        // view for buttons
        tvCancel = (TextView) view.findViewById(R.id.tv_filter_cancel);
        tvApply = (TextView) view.findViewById(R.id.tv_filter_apply);

        // listener 등록
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeView(false);
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPref.put(SsomPreferences.PREF_FILTER_AGE, age);
                filterPref.put(SsomPreferences.PREF_FILTER_PEOPLE, people);
                closeView(true);
            }
        });

        initView();

        return view;
    }

    // setting init information
    private void initView() {
        Log.i(TAG, "initView() age : " + age);
        switch(age) {
            case 20 :
                tvTwentyEarly.setSelected(true);
                break;
            case 25 :
                tvTwentyMiddle.setSelected(true);
                break;
            case 29 :
                tvTwentyLate.setSelected(true);
                break;
            case 30 :
                tvThirtyAll.setSelected(true);
                break;
        }

        Log.i(TAG, "initView() people : " + people);
        switch(people) {
            case 1 :
                tvOnePeople.setSelected(true);
                break;
            case 2 :
                tvTwoPeople.setSelected(true);
                break;
            case 3 :
                tvThreePeople.setSelected(true);
                break;
            case 4 :
                tvFourPeopleOrMore.setSelected(true);
                break;
        }
    }

    public void closeView(boolean isApply) {
        if(isApply) {
            filterPref.put(SsomPreferences.PREF_FILTER_AGE, age);
            filterPref.put(SsomPreferences.PREF_FILTER_PEOPLE, people);
        }

        if (mListener != null) {
            mListener.onFilterFragmentInteraction(isApply);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilterFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFilterFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    View.OnClickListener filterAgeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == tvTwentyEarly) {
                tvTwentyEarly.setSelected(true);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(false);
                age = 20;
            } else if(v == tvTwentyMiddle) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(true);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(false);
                age = 25;
            } else if(v == tvTwentyLate) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(true);
                tvThirtyAll.setSelected(false);
                age = 29;
            } else if(v == tvThirtyAll) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(true);
                age = 30;
            }
        }
    };

    View.OnClickListener filterPeopleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == tvOnePeople) {
                tvOnePeople.setSelected(true);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(false);
                people = 1;
            } else if(v == tvTwoPeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(true);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(false);
                people = 2;
            } else if(v == tvThreePeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(true);
                tvFourPeopleOrMore.setSelected(false);
                people = 3;
            } else if(v == tvFourPeopleOrMore) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(true);
                people = 4;
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFilterFragmentInteractionListener {
        public void onFilterFragmentInteraction(boolean isApply);
    }

}
