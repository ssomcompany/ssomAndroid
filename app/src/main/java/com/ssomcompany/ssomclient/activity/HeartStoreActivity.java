package com.ssomcompany.ssomclient.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.util.Inventory;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.InAppBillingHelper;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.ChatRoomListFragment;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.CreateChattingRoom;
import com.ssomcompany.ssomclient.network.api.SsomChatUnreadCount;
import com.ssomcompany.ssomclient.network.api.SsomMeetingRequest;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

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
            UiUtils.makeToastMessage(this, "결제가 진행 중 입니다.");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        billingHelper.disposeHelper();
    }
}
