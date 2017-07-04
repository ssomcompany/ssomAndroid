package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
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

import com.google.gson.Gson;
import com.skplanet.dodo.IapPlugin;
import com.skplanet.dodo.IapResponse;
import com.skplanet.dodo.helper.PaymentParams;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.RetrofitManager;
import com.ssomcompany.ssomclient.network.api.UserService;
import com.ssomcompany.ssomclient.network.model.HeartResult;
import com.ssomcompany.ssomclient.purchase.StoreProductListRequest;
import com.ssomcompany.ssomclient.purchase.StorePurchaseResponse;
import com.ssomcompany.ssomclient.purchase.model.StoreParam;
import com.ssomcompany.ssomclient.purchase.model.StoreProduct;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.MessageManager;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ssomcompany.ssomclient.common.CommonConst.oneAppId;

public class HeartStoreTabFragment extends RetainedStateFragment implements View.OnClickListener, MessageCountCheck {
    private static final String TAG = HeartStoreTabFragment.class.getSimpleName();

    private TextView tvHeartCount;
    private TextView tvHeartRefillTime;

    private TextView firstPrice;
    private TextView secondPrice;
    private TextView thirdPrice;
    private TextView fourthPrice;

    // heart timer 관련
    private CountDownTimer timerTask;
    private boolean timerIsRunning;

    // one store 결제 관련
    private IapPlugin mPlugin;

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
        firstPrice = (TextView) view.findViewById(R.id.heart_price_first);
        secondPrice = (TextView) view.findViewById(R.id.heart_price_second);
        thirdPrice = (TextView) view.findViewById(R.id.heart_price_third);
        fourthPrice = (TextView) view.findViewById(R.id.heart_price_fourth);

        StoreProductListRequest request = new StoreProductListRequest();
        request.setMethod("request_product_info");
        request.setParam(new StoreParam().setAppid(oneAppId).setProduct_id(new ArrayList<String>()));
        mPlugin.sendCommandRequest(new Gson().toJson(request),
                new IapPlugin.RequestCallback() {
                    @Override
                    public void onError(String s, String s1, String s2) {
                        Log.e(TAG, s1);
                        UiUtils.makeToastMessage(getActivity(), s2);
                    }

                    @Override
                    public void onResponse(IapResponse data) {
                        if (data == null || data.getContentLength() == 0) {
                            UiUtils.makeToastMessage(getActivity(), "스토어에서 조회된 결과가 없습니다.");
                            return;
                        }

                        Gson gson = new Gson();
                        StorePurchaseResponse response = gson.fromJson(data.getContentToString(), StorePurchaseResponse.class);
                        if(response == null || response.getResult() == null) {
                            UiUtils.makeToastMessage(getActivity(), "데이터 변환에 실패하였습니다.");
                            return;
                        }

                        if("0000".equals(response.getResult().getCode())) {
                            String price;
                            for(StoreProduct product : response.getResult().getProduct()) {
                                price = String.format(Locale.getDefault(), "￦%,d", Math.round(product.getPrice()));
                                switch (product.getId()) {
                                    case CommonConst.HEART_2 :
                                        firstPrice.setText(price);
                                        break;
                                    case CommonConst.HEART_8 :
                                        secondPrice.setText(price);
                                        break;
                                    case CommonConst.HEART_17 :
                                        thirdPrice.setText(price);
                                        break;
                                    case CommonConst.HEART_28 :
                                        fourthPrice.setText(price);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
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
            // 하트가 2개 미만인 유져가 앱을 새로 설치해서 들어온 경우 0,1 개 에서 refill time 이 0임.. 그럼 또 쓸경우 다시 셋팅이 되는 문제가 생김
            long refillTime = getSession().getLong(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, 0);
            if(refillTime == 0) {
                getSession().put(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, System.currentTimeMillis());
            }
            timerTask = new CountDownTimer(Util.getRefillTime(refillTime), 1000) {
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
                    RetrofitManager.getInstance().create(UserService.class)
                            .addHeart(String.valueOf(1), "android", "automatic")
                            .enqueue(new Callback<HeartResult>() {
                                @Override
                                public void onResponse(Call<HeartResult> call, Response<HeartResult> response) {
                                    if(response.isSuccessful()) {
                                        Log.d(TAG, "success... to 4hour's heart");
                                        getSession().put(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME,
                                                response.body() != null && response.body().getHeartsCount() < 2 ? System.currentTimeMillis() : 0);

                                        Intent intent = new Intent();
                                        intent.setAction(MessageManager.BROADCAST_HEART_COUNT_CHANGE);
                                        intent.putExtra(MessageManager.EXTRA_KEY_HEART_COUNT, response.body().getHeartsCount());
                                        LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);
                                    } else {
                                        showErrorMessage();
                                    }
                                }

                                @Override
                                public void onFailure(Call<HeartResult> call, Throwable t) {

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
            final String itemId;
            switch (v.getId()) {
                case R.id.layout_heart_first:
                    itemId = CommonConst.HEART_2;
                    break;
                case R.id.layout_heart_second:
                    itemId = CommonConst.HEART_8;
                    break;
                case R.id.layout_heart_third:
                    itemId = CommonConst.HEART_17;
                    break;
                case R.id.layout_heart_fourth:
                    itemId = CommonConst.HEART_28;
                    break;
                default:
                    itemId = CommonConst.HEART_2;
                    break;
            }

            PaymentParams params = new PaymentParams.Builder("appid=" + oneAppId, "product_id=" + itemId).build();
            mPlugin.sendPaymentRequest(new IapPlugin.RequestCallback() {
                        @Override
                        public void onError(String s, String s1, String s2) {
                            Log.e(TAG, s1);
                            UiUtils.makeToastMessage(getActivity(), s2);
                        }

                        @Override
                        public void onResponse(IapResponse data) {
                            if (data == null || data.getContentLength() == 0) {
                                UiUtils.makeToastMessage(getActivity(), "스토어에서 조회된 결과가 없습니다.");
                                return;
                            }

                            Gson gson = new Gson();
                            StorePurchaseResponse response = gson.fromJson(data.getContentToString(), StorePurchaseResponse.class);
                            if(response == null || response.getResult() == null) {
                                UiUtils.makeToastMessage(getActivity(), "데이터 변환에 실패하였습니다.");
                                return;
                            }

                            if("0000".equals(response.getResult().getCode())) {
                                showProgressDialog(false);
                                RetrofitManager.getInstance().create(UserService.class)
                                        .addHeart(String.valueOf(getHeartCount(itemId)), "android", response.getResult().getTxid())
                                        .enqueue(new Callback<HeartResult>() {
                                            @Override
                                            public void onResponse(Call<HeartResult> call, Response<HeartResult> response) {
                                                if(response.isSuccessful()) {
                                                    Log.d(TAG, "success... : " + response.body().toString());

                                                    Intent intent = new Intent();
                                                    intent.setAction(MessageManager.BROADCAST_HEART_COUNT_CHANGE);
                                                    intent.putExtra(MessageManager.EXTRA_KEY_HEART_COUNT, response.body().getHeartsCount());
                                                    LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);

                                                    UiUtils.makeToastMessage(getActivity(), "구매해주셔서 감사합니다. 하트를 채워드릴게욤 =)");
                                                } else {
                                                    showErrorMessage();
                                                }
                                                dismissProgressDialog();
                                            }

                                            @Override
                                            public void onFailure(Call<HeartResult> call, Throwable t) {

                                            }
                                        });
                            }
                        }
                    }, params);
        } catch (IllegalStateException e) {
            UiUtils.makeToastMessage(getActivity(), "결제가 진행 중 입니다.");
            e.printStackTrace();
        }
    }

    private int getHeartCount(String itemId) {
        int count;
        switch (itemId) {
            case CommonConst.HEART_2 :
                count = 2;
                break;
            case CommonConst.HEART_8 :
                count = 8;
                break;
            case CommonConst.HEART_17 :
                count = 17;
                break;
            case CommonConst.HEART_28 :
                count = 28;
                break;
            default:
                count = 0;
                break;
        }
        return count;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called from fragment : " + requestCode + ", resultCode : " + resultCode);

//        if(resultCode == Activity.RESULT_OK && requestCode == InAppBillingHelper.REQUEST_CODE) {
//            billingHelper.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        if(timerTask != null && timerIsRunning) {
            timerTask.cancel();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // injection plugin
        mPlugin = IapPlugin.getPlugin(context, IapPlugin.RELEASE_MODE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPlugin = IapPlugin.getPlugin(activity, IapPlugin.RELEASE_MODE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // release instance
        mPlugin.exit();
    }
}
