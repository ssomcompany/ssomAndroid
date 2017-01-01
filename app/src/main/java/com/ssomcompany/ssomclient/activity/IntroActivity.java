package com.ssomcompany.ssomclient.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.onesignal.OneSignal;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetApplicationVersion;
import com.ssomcompany.ssomclient.network.api.SsomLogin;
import com.ssomcompany.ssomclient.network.api.SsomLoginWithoutID;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

public class IntroActivity extends BaseActivity {
    private static final int REQUEST_SSOM_LOGIN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        APICaller.getApplicationVersion(new NetworkManager.NetworkListener<SsomResponse<GetApplicationVersion.Response>>() {
            @Override
            public void onResponse(SsomResponse<GetApplicationVersion.Response> response) {
                if(response.isSuccess() && response.getData() != null) {
                    String appVersion = response.getData().getVersion();
                    appVersion = appVersion.replace(".", "");
                    try {
                        if(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode < Integer.parseInt(appVersion)) {
                            // 업데이트 버전이 있는 경우 마켓으로 이동 시킴
                            UiUtils.makeCommonDialog(IntroActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                                    R.string.version_update_needed, R.style.ssom_font_16_custom_666666,
                                    R.string.update, R.string.finish,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // google market 으로 이동
                                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                            marketIntent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                            startActivity(marketIntent);
                                            finish();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            UiUtils.makeToastMessage(getApplicationContext(), "업데이트 진행 후 사용해 주시기 바랍니다 =)");
                                            finish();
                                        }
                                    });
                        } else {
                            // 앱 실행 시 최초 로그인 진행. 토큰이 있으면 로그인 건너 뜀
                            if(getSession() == null || TextUtils.isEmpty(getToken()) || TextUtils.isEmpty(getUserId())) {
                                showProgressDialog();
                                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                    @Override
                                    public void idsAvailable(String userId, String registrationId) {
                                        Log.d(TAG, "User:" + userId);
                                        if (registrationId != null) Log.d(TAG, "registrationId:" + registrationId);

                                        if(TextUtils.isEmpty(userId)) {
                                            UiUtils.makeToastMessage(IntroActivity.this, "푸시서비스에 연결할 수 없습니다. 다시 확인하시고 시도해주세요.");
                                            finish();
                                            return;
                                        }

                                        startLoginWithoutId(userId);
                                    }
                                });
                            } else {
                                startMainActivity();
                            }
                        }
                    } catch(PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    showErrorMessage();
                    finish();
                }
            }
        });
    }

    private void startLoginWithoutId(final String userId) {
        APICaller.ssomLoginWithoutID(userId, new NetworkManager.NetworkListener<SsomResponse<SsomLoginWithoutID.Response>>() {
            @Override
            public void onResponse(SsomResponse<SsomLoginWithoutID.Response> response) {
                if(response.isSuccess() && response.getData() != null) {
                    SsomLoginWithoutID.Response data = response.getData();
                    setSessionInfo(data.getToken(), data.getUserId(),
                            data.getProfileImgUrl() == null ? "" : data.getProfileImgUrl(), data.getHearts());

                    startMainActivity();
                } else {
                    UiUtils.makeCommonDialog(IntroActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                            R.string.login_failed, R.style.ssom_font_16_custom_666666,
                            R.string.login_retry, R.string.finish,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 다시 로그인하기
                                    startLoginWithoutId(userId);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UiUtils.makeToastMessage(getApplicationContext(), getString(R.string.request_to_admin));
                                    finish();
                                }
                            });

                    Log.e(TAG, "Response error with code " + response.getStatusCode() +
                            ", message : " + response.getMessage(), response.getError());
                }
                dismissProgressDialog();
            }
        });
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if(getIntent() != null && getIntent().getExtras() != null) {
                    i.putExtra(CommonConst.Intent.IS_FROM_NOTI,
                            getIntent().getBooleanExtra(CommonConst.Intent.IS_FROM_NOTI, false));
                }
                startActivity(i);
            }
        }, 1000);
    }
}
