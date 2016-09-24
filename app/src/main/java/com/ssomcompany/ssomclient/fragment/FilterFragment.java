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
    private static int age;
    private static int people;

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

                // people filter
                tvOnePeople.setSelected(true);
                tvTwoPeople.setSelected(true);
                tvThreePeople.setSelected(true);
                tvFourPeopleOrMore.setSelected(true);
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
                    age = FilterType.twentyEarly.getValue();
                    break;
                case R.id.tv_filter_age_20_middle :
                    age = FilterType.twentyMiddle.getValue();
                    break;
                case R.id.tv_filter_age_20_late :
                    age = FilterType.twentyLate.getValue();
                    break;
                case R.id.tv_filter_age_30_all :
                    age = FilterType.thirtyOver.getValue();
                    break;
                case R.id.tv_filter_people_1 :
                    people = FilterType.onePerson.getValue();
                    break;
                case R.id.tv_filter_people_2 :
                    people = FilterType.twoPeople.getValue();
                    break;
                case R.id.tv_filter_people_3 :
                    people = FilterType.threePeople.getValue();
                    break;
                case R.id.tv_filter_people_4_n_over :
                    people = FilterType.fourPeople.getValue();
                    break;
            }
        }
    };
}
