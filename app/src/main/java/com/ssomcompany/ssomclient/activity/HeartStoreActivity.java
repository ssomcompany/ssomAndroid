package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.vending.billing.util.Inventory;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.control.InAppBillingHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class HeartStoreActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = HeartStoreActivity.class.getSimpleName();

    private String[] items = new String[]{
            CommonConst.HEART_2,
            CommonConst.HEART_8,
            CommonConst.HEART_17,
            CommonConst.HEART_28
    };

    private InAppBillingHelper billingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_store);

        final TextView firstPrice = (TextView) findViewById(R.id.heart_price_first);
        final TextView secondPrice = (TextView) findViewById(R.id.heart_price_second);
        final TextView thirdPrice = (TextView) findViewById(R.id.heart_price_third);
        final TextView fourthPrice = (TextView) findViewById(R.id.heart_price_fourth);

        billingHelper = new InAppBillingHelper(this, getString(R.string.in_app_purchase_id));
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
                UiUtils.makeToastMessage(getApplicationContext(), "마켓에 연결하는데 실패하였습니다.");
                finish();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_close_top).setOnClickListener(this);
        findViewById(R.id.layout_heart_first).setOnClickListener(this);
        findViewById(R.id.layout_heart_second).setOnClickListener(this);
        findViewById(R.id.layout_heart_third).setOnClickListener(this);
        findViewById(R.id.layout_heart_fourth).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        showProgressDialog();
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
                case R.id.btn_close:
                case R.id.btn_close_top:
                    finish();
                    break;
            }
        } catch (IllegalStateException e) {
            dismissProgressDialog();
            UiUtils.makeToastMessage(this, "결제가 진행 중 입니다.");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == InAppBillingHelper.REQUEST_CODE) {
            billingHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            dismissProgressDialog();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        billingHelper.disposeHelper();
    }
}
