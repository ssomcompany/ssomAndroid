package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.FilterType;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.control.ViewListener;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewListener.OnFilterFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FilterFragment extends Fragment {
    private static final String TAG = "FilterFragment";

    // set preferences
    private SsomPreferences filterPref;

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
    private static ArrayList<String> ageArr;
    private static ArrayList<String> peopleArr;

    private ViewListener.OnFilterFragmentInteractionListener mListener;

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

        String[] baseAge = {FilterType.twentyEarly.getValue(), FilterType.twentyMiddle.getValue(),
                FilterType.twentyLate.getValue(), FilterType.thirtyOver.getValue()};

        String[] basePeople = {FilterType.onePerson.getValue(), FilterType.twoPeople.getValue(),
                FilterType.threePeople.getValue(), FilterType.fourPeople.getValue()};

        ageArr = filterPref.getStringArray(SsomPreferences.PREF_FILTER_AGE, new ArrayList<>(Arrays.asList(baseAge)));
        peopleArr = filterPref.getStringArray(SsomPreferences.PREF_FILTER_PEOPLE, new ArrayList<>(Arrays.asList(basePeople)));

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

        tvTwentyEarly.setOnClickListener(filterItemClickListener);
        tvTwentyMiddle.setOnClickListener(filterItemClickListener);
        tvTwentyLate.setOnClickListener(filterItemClickListener);
        tvThirtyAll.setOnClickListener(filterItemClickListener);

        tvOnePeople.setOnClickListener(filterItemClickListener);
        tvTwoPeople.setOnClickListener(filterItemClickListener);
        tvThreePeople.setOnClickListener(filterItemClickListener);
        tvFourPeopleOrMore.setOnClickListener(filterItemClickListener);

        // view for buttons
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_filter_cancel);
        TextView tvApply = (TextView) view.findViewById(R.id.tv_filter_apply);

        // listener 등록
        view.findViewById(R.id.btn_select_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // age filter
                tvTwentyEarly.setSelected(true);
                tvTwentyMiddle.setSelected(true);
                tvTwentyLate.setSelected(true);
                tvThirtyAll.setSelected(true);
                if(!ageArr.contains(FilterType.twentyEarly.getValue())) ageArr.add(FilterType.twentyEarly.getValue());
                if(!ageArr.contains(FilterType.twentyMiddle.getValue())) ageArr.add(FilterType.twentyMiddle.getValue());
                if(!ageArr.contains(FilterType.twentyLate.getValue())) ageArr.add(FilterType.twentyLate.getValue());
                if(!ageArr.contains(FilterType.thirtyOver.getValue())) ageArr.add(FilterType.thirtyOver.getValue());
                Log.d(TAG, "ageArr : " + ageArr.size());

                // people filter
                tvOnePeople.setSelected(true);
                tvTwoPeople.setSelected(true);
                tvThreePeople.setSelected(true);
                tvFourPeopleOrMore.setSelected(true);
                if(!peopleArr.contains(FilterType.onePerson.getValue())) peopleArr.add(FilterType.onePerson.getValue());
                if(!peopleArr.contains(FilterType.twoPeople.getValue())) peopleArr.add(FilterType.twoPeople.getValue());
                if(!peopleArr.contains(FilterType.threePeople.getValue())) peopleArr.add(FilterType.threePeople.getValue());
                if(!peopleArr.contains(FilterType.fourPeople.getValue())) peopleArr.add(FilterType.fourPeople.getValue());
                Log.d(TAG, "people : " + peopleArr.size());
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeView(false);
            }
        });
        view.findViewById(R.id.icon_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeView(false);
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeView(true);
            }
        });

        initView();

        return view;
    }

    // setting init information
    private void initView() {
        Log.i(TAG, "initView() age : " + ageArr.size());
        for(String age : ageArr) {
            if (FilterType.twentyEarly.getValue().equals(age)) {
                tvTwentyEarly.setSelected(true);
            } else if (FilterType.twentyMiddle.getValue().equals(age)) {
                tvTwentyMiddle.setSelected(true);
            } else if (FilterType.twentyLate.getValue().equals(age)) {
                tvTwentyLate.setSelected(true);
            } else if (FilterType.thirtyOver.getValue().equals(age)) {
                tvThirtyAll.setSelected(true);
            }
        }

        Log.i(TAG, "initView() people : " + peopleArr.size());
        for(String people : peopleArr) {
            if (FilterType.onePerson.getValue().equals(people)) {
                tvOnePeople.setSelected(true);
            } else if (FilterType.twoPeople.getValue().equals(people)) {
                tvTwoPeople.setSelected(true);
            } else if (FilterType.threePeople.getValue().equals(people)) {
                tvThreePeople.setSelected(true);
            } else if (FilterType.fourPeople.getValue().equals(people)) {
                tvFourPeopleOrMore.setSelected(true);
            }
        }
    }

    public void closeView(boolean isApply) {
        if(isApply) {
            filterPref.put(SsomPreferences.PREF_FILTER_AGE, ageArr);
            filterPref.put(SsomPreferences.PREF_FILTER_PEOPLE, peopleArr);
        }

        if (mListener != null) {
            mListener.onFilterFragmentInteraction(isApply);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mListener = (ViewListener.OnFilterFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFilterFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ViewListener.OnFilterFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFilterFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    View.OnClickListener filterItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setSelected(!v.isSelected());

            switch (v.getId()) {
                case R.id.tv_filter_age_20_early :
                    if(v.isSelected()) {
                        ageArr.add(FilterType.twentyEarly.getValue());
                    } else {
                        ageArr.remove(FilterType.twentyEarly.getValue());
                    }
                    break;
                case R.id.tv_filter_age_20_middle :
                    if(v.isSelected()) {
                        ageArr.add(FilterType.twentyMiddle.getValue());
                    } else {
                        ageArr.remove(FilterType.twentyMiddle.getValue());
                    }
                    break;
                case R.id.tv_filter_age_20_late :
                    if(v.isSelected()) {
                        ageArr.add(FilterType.twentyLate.getValue());
                    } else {
                        ageArr.remove(FilterType.twentyLate.getValue());
                    }
                    break;
                case R.id.tv_filter_age_30_all :
                    if(v.isSelected()) {
                        ageArr.add(FilterType.thirtyOver.getValue());
                    } else {
                        ageArr.remove(FilterType.thirtyOver.getValue());
                    }
                    break;
                case R.id.tv_filter_people_1 :
                    if(v.isSelected()) {
                        peopleArr.add(FilterType.onePerson.getValue());
                    } else {
                        peopleArr.remove(FilterType.onePerson.getValue());
                    }
                    break;
                case R.id.tv_filter_people_2 :
                    if(v.isSelected()) {
                        peopleArr.add(FilterType.twoPeople.getValue());
                    } else {
                        peopleArr.remove(FilterType.twoPeople.getValue());
                    }
                    break;
                case R.id.tv_filter_people_3 :
                    if(v.isSelected()) {
                        peopleArr.add(FilterType.threePeople.getValue());
                    } else {
                        peopleArr.remove(FilterType.threePeople.getValue());
                    }
                    break;
                case R.id.tv_filter_people_4_n_over :
                    if(v.isSelected()) {
                        peopleArr.add(FilterType.fourPeople.getValue());
                    } else {
                        peopleArr.remove(FilterType.fourPeople.getValue());
                    }
                    break;
            }
        }
    };
}
