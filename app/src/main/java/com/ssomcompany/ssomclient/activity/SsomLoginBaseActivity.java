package com.ssomcompany.ssomclient.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.LoginFragment;
import com.ssomcompany.ssomclient.fragment.LoginRegistFragment;

/**
 * Created by AaronMac on 2016. 7. 25..
 */
public class SsomLoginBaseActivity extends BaseActivity implements ViewListener.OnLoginFragmentInteractionListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fragmentManager = getSupportFragmentManager();
        LoginFragment loginFragment = new LoginFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.login_container, loginFragment, CommonConst.LOGIN_FRAGMENT).commit();
    }

    @Override
    public void onLoginFragmentInteraction(int resId) {

        switch (resId) {
            case R.id.btn_login :
                // 로그인 action
            case R.id.btn_login_find_password :
                // 비밀번호 찾기 화면으로 이동
            case R.id.btn_login_register:
                // 회원 가입 화면으로 이동
            case R.id.btn_register:
                // 회원 가입 action
                LoginRegistFragment loginRegistFragment = new LoginRegistFragment();
                // TODO - add to backstack if needed
                fragmentManager.beginTransaction()
                        .replace(R.id.login_container, loginRegistFragment, CommonConst.LOGIN_REGIST_FRAGMENT).commit();
                break;
            default :
                Log.d(TAG, "default, do nothing..");
                break;
        }
    }
}
