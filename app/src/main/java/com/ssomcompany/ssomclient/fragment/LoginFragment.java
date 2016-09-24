package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.SsomLogin;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

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

    // email, password, password confirm must not be empty
    private boolean checkEmptyBox() {
        if(TextUtils.isEmpty(etLoginEmail.getText())) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.login_reg_email_hint));
            return true;
        } else if(TextUtils.isEmpty(etLoginPass.getText())) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.login_reg_password_hint));
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                if(checkEmptyBox()) return;

                showProgressDialog();
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        Log.d(TAG, "User:" + userId);
                        if (registrationId != null) Log.d(TAG, "registrationId:" + registrationId);

                        if(TextUtils.isEmpty(userId)) {
                            Log.d(TAG, "User id is invalid.. cannot log in");
                            dismissProgressDialog();
                            return;
                        }

                        APICaller.ssomLogin(String.valueOf(etLoginEmail.getText()), String.valueOf(etLoginPass.getText()),
                                userId, new NetworkManager.NetworkListener<SsomResponse<SsomLogin.Response>>() {
                                    @Override
                                    public void onResponse(SsomResponse<SsomLogin.Response> response) {
                                        if(response.isSuccess()) {
                                            if(response.getData() != null) {
                                                setSessionInfo(response.getData().getToken(),
                                                        String.valueOf(etLoginEmail.getText()), response.getData().getUserId());
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


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mListener = (ViewListener.OnLoginFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnLoginFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ViewListener.OnLoginFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }
}
