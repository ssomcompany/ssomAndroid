package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.SsomLogin;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

/**
 * Created by AaronMac on 2016. 7. 28..
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private ViewListener.OnLoginFragmentInteractionListener mListener;

    EditText etLoginEmail;
    EditText etLoginPass;

    TextView btnLogin;
    TextView btnFindPass;
    TextView btnRegister;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);
        etLoginEmail = (EditText) loginView.findViewById(R.id.et_login_email);
        etLoginPass = (EditText) loginView.findViewById(R.id.et_login_password);
        btnLogin = (TextView) loginView.findViewById(R.id.btn_login);
        btnFindPass = (TextView) loginView.findViewById(R.id.btn_login_find_password);
        btnRegister = (TextView) loginView.findViewById(R.id.btn_login_register);

        btnLogin.setOnClickListener(this);
        btnFindPass.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        return loginView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                showProgressDialog();
                APICaller.ssomLogin(String.valueOf(etLoginEmail.getText()), String.valueOf(etLoginPass.getText()),
                        new NetworkManager.NetworkListener<SsomResponse<SsomLogin.Response>>() {
                            @Override
                            public void onResponse(SsomResponse<SsomLogin.Response> response) {
                                if(response.isSuccess()) {
                                    if(response.getData() != null) {
                                        getSession().put(SsomPreferences.PREF_SESSION_TOKEN, response.getData().getToken());
                                        getSession().put(SsomPreferences.PREF_SESSION_EMAIL, etLoginEmail.getText().toString());
                                        mListener.onLoginFragmentInteraction(R.id.btn_login);
                                    } else {
                                        Log.e(TAG, "unexpected error, data is null");
                                    }
                                } else {
                                    Log.e(TAG, "Response error with code " + response.getStatusCode() +
                                            ", message : " + response.getMessage(), response.getError());

                                    if(response.getStatusCode() == 401) {
                                        UiUtils.makeToastMessage(getActivity(),
                                                getString(R.string.login_failed));
                                    } else {
                                        showErrorMessage();
                                    }
                                }
                                dismissProgressDialog();
                            }
                        });
                break;
            case R.id.btn_login_find_password :
            case R.id.btn_login_register :
                mListener.onLoginFragmentInteraction(v.getId());
                break;
            default :
                Log.d(TAG, "default, do nothing..");
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ViewListener.OnLoginFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }
}
