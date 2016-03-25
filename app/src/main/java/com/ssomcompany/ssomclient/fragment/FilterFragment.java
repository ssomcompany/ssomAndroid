package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.FilterType;
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

        age = filterPref.getInt(SsomPreferences.PREF_FILTER_AGE, FilterType.twentyEarly.getValue());
        people = filterPref.getInt(SsomPreferences.PREF_FILTER_PEOPLE, FilterType.onePerson.getValue());

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
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_filter_cancel);
        TextView tvApply = (TextView) view.findViewById(R.id.tv_filter_apply);

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
        if(age == FilterType.twentyEarly.getValue()) {
            tvTwentyEarly.setSelected(true);
        } else if(age == FilterType.twentyMiddle.getValue()) {
            tvTwentyMiddle.setSelected(true);
        } else if(age == FilterType.twentyLate.getValue()) {
            tvTwentyLate.setSelected(true);
        } else if(age == FilterType.thirtyOver.getValue()) {
            tvThirtyAll.setSelected(true);
        }

        Log.i(TAG, "initView() people : " + people);
        if(people == FilterType.onePerson.getValue()) {
            tvOnePeople.setSelected(true);
        } else if(people == FilterType.twoPeople.getValue()) {
            tvTwoPeople.setSelected(true);
        } else if(people == FilterType.threePeople.getValue()) {
            tvThreePeople.setSelected(true);
        } else if(people == FilterType.fourPeople.getValue()) {
            tvFourPeopleOrMore.setSelected(true);
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
                age = FilterType.twentyEarly.getValue();
            } else if(v == tvTwentyMiddle) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(true);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(false);
                age = FilterType.twentyMiddle.getValue();
            } else if(v == tvTwentyLate) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(true);
                tvThirtyAll.setSelected(false);
                age = FilterType.twentyLate.getValue();
            } else if(v == tvThirtyAll) {
                tvTwentyEarly.setSelected(false);
                tvTwentyMiddle.setSelected(false);
                tvTwentyLate.setSelected(false);
                tvThirtyAll.setSelected(true);
                age = FilterType.thirtyOver.getValue();
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
                people = FilterType.onePerson.getValue();
            } else if(v == tvTwoPeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(true);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(false);
                people = FilterType.twoPeople.getValue();
            } else if(v == tvThreePeople) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(true);
                tvFourPeopleOrMore.setSelected(false);
                people = FilterType.threePeople.getValue();
            } else if(v == tvFourPeopleOrMore) {
                tvOnePeople.setSelected(false);
                tvTwoPeople.setSelected(false);
                tvThreePeople.setSelected(false);
                tvFourPeopleOrMore.setSelected(true);
                people = FilterType.fourPeople.getValue();
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
        void onFilterFragmentInteraction(boolean isApply);
    }

}
