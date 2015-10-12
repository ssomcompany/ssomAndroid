package com.ssomcompany.ssomclient;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
