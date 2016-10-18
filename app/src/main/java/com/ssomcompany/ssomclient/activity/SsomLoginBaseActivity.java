package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.LoginFragment;
import com.ssomcompany.ssomclient.fragment.LoginRegistFragment;
import com.ssomcompany.ssomclient.network.APICaller;

/**
 * Created by AaronMac on 2016. 7. 25..
 */
public class SsomLoginBaseActivity extends BaseActivity implements View.OnClickListener, ViewListener.OnLoginFragmentInteractionListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // X button click event
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        LoginFragment loginFragment = new LoginFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.login_container, loginFragment, CommonConst.LOGIN_FRAGMENT).commit();
    }

    @Override
    public void onLoginFragmentInteraction(int resId) {

        switch (resId) {
            case R.id.btn_login :
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_login_find_password :
                // 비밀번호 찾기 화면으로 이동
                break;
            case R.id.btn_login_register :
                // 회원 가입 화면으로 이동
                LoginRegistFragment loginRegistFragment = new LoginRegistFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.login_container, loginRegistFragment, CommonConst.LOGIN_REGIST_FRAGMENT)
                        .addToBackStack(CommonConst.LOGIN_FRAGMENT).commit();
                break;
            case R.id.btn_register :
                // 회원 가입 action
                fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.LOGIN_REGIST_FRAGMENT)).commit();
                fragmentManager.popBackStack();
                break;
            default :
                Log.d(TAG, "default, do nothing..");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_cancel) finish();
    }
}
