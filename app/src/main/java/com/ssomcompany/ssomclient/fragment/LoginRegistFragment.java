package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.SsomRegisterUser;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

/**
 * Created by AaronMac on 2016. 7. 27..
 */
public class LoginRegistFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = LoginFragment.class.getSimpleName();

    // should set password length
    static final int MIN_PASSWORD_LENGTH = 6;

    private ViewListener.OnLoginFragmentInteractionListener mListener;

    TextView tvEmailAlreadyExist;
    TextView tvPasswordNotMatch;
    EditText etLoginRegEmail;
    EditText etLoginRegPass;
    EditText etLoginRegPassConfirm;

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
        View registerView = inflater.inflate(R.layout.fragment_regitst, container, false);
        tvEmailAlreadyExist = (TextView) registerView.findViewById(R.id.tv_email_already_exist);
        tvPasswordNotMatch = (TextView) registerView.findViewById(R.id.tv_password_not_match);
        etLoginRegEmail = (EditText) registerView.findViewById(R.id.et_login_reg_email);
        etLoginRegPass = (EditText) registerView.findViewById(R.id.et_login_reg_password);
        etLoginRegPassConfirm = (EditText) registerView.findViewById(R.id.et_login_reg_pass_confirm);
        btnRegister = (TextView) registerView.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(this);

        return registerView;
    }

    // email, password, password confirm must not be empty
    private boolean checkEmptyBox() {
        if(TextUtils.isEmpty(etLoginRegEmail.getText())) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.login_reg_email_hint));
            return true;
        } else if(TextUtils.isEmpty(etLoginRegPass.getText())) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.login_reg_password_hint));
            return true;
        } else if(TextUtils.isEmpty(etLoginRegPassConfirm.getText())) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.login_reg_password_confirm_hint));
            return true;
        }
        return false;
    }

    private boolean checkPasswordMatched(){
        return etLoginRegPass.getText().toString().equals(etLoginRegPassConfirm.getText().toString());
    }

    private boolean checkPossiblePassword() {
        String password = String.valueOf(etLoginRegPass.getText());

        // password 길이 확인
        if(password.length() < MIN_PASSWORD_LENGTH) {
            UiUtils.makeToastMessage(getActivity(),
                    String.format(getString(R.string.password_should_be_over_n_letters), MIN_PASSWORD_LENGTH));
            return false;
        }

        // password 형식 체크
        if(!Util.validatePassword(password)) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.invalid_password_format));
            return false;
        }

        return true;
    }

    private boolean checkPossibleEmail() {
        String email = String.valueOf(etLoginRegEmail.getText());

        if(!Util.validateEmail(email)) {
            UiUtils.makeToastMessage(getActivity(), getString(R.string.invalid_email_format));
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                // email, password, password confirm return check
                if(checkEmptyBox() || !checkPossibleEmail()
                        || !checkPossiblePassword() || !checkPasswordMatched()) return;

                showProgressDialog();
                APICaller.ssomRegisterUser(String.valueOf(etLoginRegEmail.getText()), String.valueOf(etLoginRegPass.getText()),
                        new NetworkManager.NetworkListener<SsomResponse<SsomRegisterUser.Response>>() {
                            @Override
                            public void onResponse(SsomResponse<SsomRegisterUser.Response> response) {
                                if(response.isSuccess()) {
                                    UiUtils.makeToastMessage(getActivity(),
                                            getString(R.string.success_registration));
                                    mListener.onLoginFragmentInteraction(R.id.btn_register);
                                } else {
                                    Log.e(TAG, "Response error with code " + response.getResultCode() +
                                            ", message : " + response.getMessage(), response.getError());
                                    showErrorMessage();
                                }
                                dismissProgressDialog();
                            }

                        });
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
