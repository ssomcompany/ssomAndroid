package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.control.InAppBillingHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class HeartStoreTabFragment extends RetainedStateFragment implements View.OnClickListener {
    private static final String TAG = HeartStoreTabFragment.class.getSimpleName();

    private String[] items = new String[]{
            CommonConst.HEART_2,
            CommonConst.HEART_8,
            CommonConst.HEART_17,
            CommonConst.HEART_28
    };

    private InAppBillingHelper billingHelper;
    private TextView tvHeartCount;

    @Override
    public void onResume() {
        super.onResume();
        if(tvHeartCount != null) {
            int color = R.color.red_pink;
            String heartCnt = getString(R.string.heart_current_count, getHeartCount());
            SpannableStringBuilder builder = new SpannableStringBuilder(heartCnt);
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(color)), 7, 7 + (getHeartCount() >= 10 ? 2 : 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 7, 7 + (getHeartCount() >= 10 ? 2 : 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvHeartCount.setText(builder);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_store, null);

        tvHeartCount = (TextView) view.findViewById(R.id.tv_heart_count);
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
        billingHelper.disposeHelper();
    }
}
