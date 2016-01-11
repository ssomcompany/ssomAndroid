package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFilterFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MainActivity activity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String ssomType = "all";
    private OnFilterFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        view.setClickable(true);

        final TextView ageText = (TextView) view.findViewById(R.id.filter_text_age_range);
        final TextView countText = (TextView) view.findViewById(R.id.filter_text_user_count);
        final SeekBar ageSeekbar = (SeekBar) view.findViewById(R.id.filter_seekbar_age_range);
        ageSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int count = progress+20;
                ageText.setText(String.valueOf(count)+"~"+String.valueOf(count+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final SeekBar countSeekbar = (SeekBar) view.findViewById(R.id.filter_seekbar_user_count);
        countSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int count = progress+1;
                countText.setText(String.valueOf(count)+"~"+String.valueOf(count+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SharedPreferences filterPref = activity.getSharedPreferences("filter", Context.MODE_PRIVATE);
        int minAge = filterPref.getInt("minAge",20);
        int minCount = filterPref.getInt("minCount",1);
        ageSeekbar.setProgress(minAge-20);
        countSeekbar.setProgress(minCount-1);
        ssomType = filterPref.getString("ssomtype","all");
        ImageView btnCancel = (ImageView) view.findViewById(R.id.filter_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonPressed(null);
            }
        });
        View btnOk =view.findViewById(R.id.filter_btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences filterPref = activity.getSharedPreferences("filter", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = filterPref.edit();
                int minAge = ageSeekbar.getProgress()+20;
                int maxAge = ageSeekbar.getProgress()+21;
                int minCount = countSeekbar.getProgress()+1;
                int maxCount = countSeekbar.getProgress()+2;
                editor.putInt("minAge",minAge);
                editor.putInt("maxAge",maxAge);
                editor.putInt("minCount",minCount);
                editor.putInt("maxCount",maxCount);
                editor.putString("ssomtype",ssomType);
                editor.commit();
                onCloseButtonPressed(null);
            }
        });
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onCloseButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFilterFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity  = (MainActivity) activity;
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

    @Override
    public void onStop() {
        super.onStop();
        activity.setWriteBtn(true);
    }

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
        // TODO: Update argument type and name
        public void onFilterFragmentInteraction(Uri uri);
    }

}
