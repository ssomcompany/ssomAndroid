package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.vending.billing.util.Inventory;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.InAppBillingHelper;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.AddHeartCount;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.MessageManager;

import java.util.ArrayList;
import java.util.Arrays;

public class HeartStoreTabFragment extends RetainedStateFragment implements View.OnClickListener, MessageCountCheck {
    private static final String TAG = HeartStoreTabFragment.class.getSimpleName();

    private String[] items = new String[]{
            CommonConst.HEART_2,
            CommonConst.HEART_8,
            CommonConst.HEART_17,
            CommonConst.HEART_28
    };

    private InAppBillingHelper billingHelper;
    private TextView tvHeartCount;
    private TextView tvHeartRefillTime;

    // heart timer 관련
    private CountDownTimer timerTask;
    private boolean timerIsRunning;

    @Override
    public void onResume() {
        super.onResume();
        setHeartCount(getHeartCount());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_store, null);

        tvHeartCount = (TextView) view.findViewById(R.id.tv_heart_count);
        tvHeartRefillTime = (TextView) view.findViewById(R.id.tv_heart_refill_time);
        final TextView firstPrice = (TextView) view.findViewById(R.id.heart_price_first);
        final TextView secondPrice = (TextView) view.findViewById(R.id.heart_price_second);
        final TextView thirdPrice = (TextView) view.findViewById(R.id.heart_price_third);
        final TextView fourthPrice = (TextView) view.findViewById(R.id.heart_price_fourth);

        billingHelper = new InAppBillingHelper((BaseActivity) getActivity(), getString(R.string.in_app_purchase_id));
        // test 아이템 구매를 위해 설정
//        billingHelper.setTest(true);
        billingHelper.startSetup(new ArrayList<>(Arrays.asList(items)), new InAppBillingHelper.InventoryLoadListener(){
            @Override
            public void onBefore() {
            }

            @Override
            public void onSuccess(Inventory inventory) {
                Log.d(TAG, "로딩에 성공하였습니다.");
                Log.d(TAG, inventory.getSkuDetails(items[0]) == null ? "null" : inventory.getSkuDetails(items[0]).toString());
                firstPrice.setText(inventory.getSkuDetails(items[0]).getPrice());
                secondPrice.setText(inventory.getSkuDetails(items[1]).getPrice());
                thirdPrice.setText(inventory.getSkuDetails(items[2]).getPrice());
                fourthPrice.setText(inventory.getSkuDetails(items[3]).getPrice());
            }

            @Override
            public void onFail() {
                UiUtils.makeToastMessage(getActivity(), "구글마켓에 연결할 수 없는 상태입니다.");
//                getActivity().finish();
            }
        });

        view.findViewById(R.id.layout_heart_first).setOnClickListener(this);
        view.findViewById(R.id.layout_heart_second).setOnClickListener(this);
        view.findViewById(R.id.layout_heart_third).setOnClickListener(this);
        view.findViewById(R.id.layout_heart_fourth).setOnClickListener(this);

        return view;
    }

    private void setHeartRefillTime(String time) {
        tvHeartRefillTime.setText(time);
    }

    public void setHeartCount(int count) {
        Log.d(TAG, "setHeartCount called");
        if(!timerIsRunning && (count == 0 || count == 1)) {
            Log.d(TAG, "setHeartCount timerTask start");
            timerTask = new CountDownTimer(Util.getRefillTime(getSession().getLong(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, 0)), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerIsRunning = true;
                    int hour = (int) millisUntilFinished / (60 * 60 * 1000);
                    int min = (int) (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
                    setHeartRefillTime("0" + hour + ":" + (min < 10 ? "0" + min : min));
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "timerTask is finished");
                    timerIsRunning = false;
                    setHeartRefillTime("00:00");
                    APICaller.addHeartCount(getToken(), 1, "automatic",
                            new NetworkManager.NetworkListener<SsomResponse<AddHeartCount.Response>>() {
                                @Override
                                public void onResponse(SsomResponse<AddHeartCount.Response> response) {
                                    if(response.isSuccess()) {
                                        Log.d(TAG, "success... to 4hour's heart");
                                        getSession().put(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, System.currentTimeMillis());

                                        Intent intent = new Intent();
                                        intent.setAction(MessageManager.BROADCAST_HEART_COUNT_CHANGE);
                                        intent.putExtra(MessageManager.EXTRA_KEY_HEART_COUNT, response.getData().getHeartsCount());
                                        LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);
                                    } else {
                                        showErrorMessage();
                                    }
                                }
                            });
                }
            }.start();
        } else {
            Log.d(TAG, "setHeartCount refill time clear");
            if(count >= 2) {
                if(timerIsRunning) {
                    timerTask.cancel();
                    timerIsRunning = false;
                }
                setHeartRefillTime("--:--");
            }
        }

        if(tvHeartCount != null) {
            int color = R.color.red_pink;
            String heartCnt = getString(R.string.heart_current_count, count);
            SpannableStringBuilder builder = new SpannableStringBuilder(heartCnt);
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(color)), 7, 7 + (count >= 10 ? 2 : 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 7, 7 + (count >= 10 ? 2 : 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvHeartCount.setText(builder);
        }
    }

    @Override
    protected void receivedHeartChange(Intent intent) {
        super.receivedHeartChange(intent);
        if(MessageManager.BROADCAST_HEART_COUNT_CHANGE.equalsIgnoreCase(intent.getAction())) {
            int count = intent.getIntExtra(MessageManager.EXTRA_KEY_HEART_COUNT, 0);
            getSession().put(SsomPreferences.PREF_SESSION_HEART, count);
            setHeartCount(count);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.layout_heart_first:
                    billingHelper.purchaseItem(CommonConst.HEART_2);
                    break;
                case R.id.layout_heart_second:
                    billingHelper.purchaseItem(CommonConst.HEART_8);
                    break;
                case R.id.layout_heart_third:
                    billingHelper.purchaseItem(CommonConst.HEART_17);
                    break;
                case R.id.layout_heart_fourth:
                    billingHelper.purchaseItem(CommonConst.HEART_28);
                    break;
            }
        } catch (IllegalStateException e) {
            UiUtils.makeToastMessage(getActivity(), "결제가 진행 중 입니다.");
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called from fragment : " + requestCode + ", resultCode : " + resultCode);

        if(resultCode == Activity.RESULT_OK && requestCode == InAppBillingHelper.REQUEST_CODE) {
            billingHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        billingHelper.disposeHelper();
        if(timerTask != null && timerIsRunning) {
            timerTask.cancel();
        }
    }
}
