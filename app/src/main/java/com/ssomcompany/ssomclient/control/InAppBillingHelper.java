package com.ssomcompany.ssomclient.control;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.AddHeartCount;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageManager;

import java.util.ArrayList;
import java.util.List;

public class InAppBillingHelper {
    public static final String TAG = "InAppBillingHelper";

    // onActivityResult에서 받을 requestCode.
    public static final int REQUEST_CODE = 1001;
    // publicKey 개발자콘솔에서 앱을 생성후 얻을 수 있다.
    private String mPublicKey;

    // 테스트를 하기 위한 테스트용 productId(SKU)
    private static final String TEST_SKU = "android.test.purchased";
    // 구매되고 소진되지 않은 아이템을 캐시해놓을 변수.
    private ArrayList<String> mOwnedItems = new ArrayList<>();

    // 가져온 util클래스에서 제공하는 클래스. 아이템리스트를 갖고 있다.
    private Inventory mInventory;

    // 가져온 util클래스의 실제 헬퍼클래스
    private IabHelper mHelper;

    private BaseActivity mActivity;

    // 테스트 여부인지.
    private boolean mIsTest;
    // 실제 우리가 알고있는 아이템목록. (어플리케이션 서버로부터 받아오면됨.)
    public List<String> mItems = new ArrayList<>();
    // 로드 이후 호출될 리스너.
    public interface InventoryLoadListener {
        void onBefore();
        void onSuccess(Inventory inventory);
        void onFail();
    }
    private void init(BaseActivity activity) {
        mActivity = activity;
        mIsTest = false;
    }
    // 공개키가 없는 생성자.
    public InAppBillingHelper(BaseActivity activity) {
        init(activity);
    }
    // 공개키가 있는 생성자.
    public InAppBillingHelper(BaseActivity activity, String publicKey) {
        mPublicKey = publicKey;
        init(activity);
    }
    // 테스트 여부의 세터.
    public void setTest(boolean isTest) {
        mIsTest = isTest;
    }

    public void startSetup(ArrayList<String> items, final InventoryLoadListener listener) {
        // before() 를 호출함 으로서 로딩바를 보여주는 등의 액션을 취할 수 있다.
        listener.onBefore();
        // 실제 구글 서버에 요청할 sku리스트.
        mItems = items;
        // 서버에서 테스트 sku목록까지 넣어놓았었다면 제외.
        for (int i = 0; i < mItems.size(); ++i) {
            if (mItems.get(i).equals(TEST_SKU)) {
                mItems.remove(mItems.get(i));
                break;
            }
        }
        // 실제 util에서 가져온 헬퍼생성.
        mHelper = new IabHelper(mActivity, mPublicKey);
        // startSetup함수를 호출함으로서 현재 통신가능여부를 확인하고, 정상적으로 커넥션이 이루어졌는지 확인할 수 있다.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    consumeItem(TEST_SKU, null);
                    listener.onFail();
                } else {
                    // 성공적으로 연결이 되었다면 이제 실질적인 아이템을 로드해야한다.
                    loadItemInventory(listener);
                }
            }
        });
    }

    // 아이템을 로드하는 메소드
    private void loadItemInventory(final InventoryLoadListener listener) {
        // 실제 아이템목록을 받아올 수 있음.
        // mItems에 올바른 값이 할당되어야 함.
        // 만약 mItems가 null값이라면 queryInventoryAsync메소드는 콜백이 호출될때 성공했다고 나오지만
        // 어떠한 아이템 리스트 값도 받아올 수 없음.
        mHelper.queryInventoryAsync(true, mItems, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                // IabResult 값에는 다양한 에러코드및 성공여부를 담고 있음.
                if (result.isSuccess()) {
                    // 멤버변수로 받아온 아이템리스트를 갖고 있는 inventory를 담음.
                    mInventory = inv;
                    // mInventory에는 구매는 했지만 소진되지 않은 값을 얻어올 수 있음.
                    // 따라서 새롭게 받아온 인벤토리를 통해 갱신하기 위해 클리어.
                    mOwnedItems.clear();
                    // 이제 mItems의 sku값을을 갖고 inv에 조회하여 소진되지 않은 아이템 목록을 담음.
                    for (String sku : mItems) {
                        if (inv.hasPurchase(sku)) {
                            mOwnedItems.add(sku);
                        }
                    }
                    // 테스트sku는 가끔 소진되지 않을 때가 있음. 이럴땐 무조건 헬퍼가 수행될 때 소진시킴.
                    if (mInventory.hasPurchase(TEST_SKU)) {
                        // consumeItem에 첫번째 인자는 해당 sku이며, 두번째 인자는 Purchase 인스턴스가 됨. 이후에 다시 설명.
                        consumeItem(TEST_SKU, null);
                    }
                    listener.onSuccess(inv);
                }
                else {
                    listener.onFail();
                }
            }
        });
    }

    // 실제 구매 (인앱결제)
    public void purchaseItem(final String sku) {
        // 만약 test가 true라면 테스트SKU를 갖고 구매요청을 한다.
        final String refinedSKU = mIsTest ? TEST_SKU : sku;
        // 구매후 소진되지 않은 아이템이라면 다시 구매할 수 없다.
        if (!mOwnedItems.contains(refinedSKU)) {
            // 실제 구매 & 결제요청
            // 해당 메소드를 호출하면 내부적으로 팝업창형태의 결제창이 나온다.
            mHelper.flagEndAsync();
            mHelper.launchPurchaseFlow(mActivity, refinedSKU, REQUEST_CODE, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, final Purchase info) {
                    if (result.isSuccess()) {
                        // 구매되고 소진되지 않은 목록에 캐시.
                        mOwnedItems.add(info.getSku());

                        // 실제 어플리케이션 서버에서 소진되도록 호출.
                        consumeItemForServer(info);
                    } else {
                        if (result.getResponse() == 0) {
                            // 구매가 실패하였고 이유가 아직 소진되지 않은 것이라면
                            // 소진을 위해 서버에 요청.
                            consumeAllItemsForServer();
                        }
                        // TODO
//                        CommonHelper.showMessage(mActivity, result.getResponse() + "");
//                        ErrorHandler.handleLocalError(mActivity, LocalErrorCode.ITEM_PURCHASE_ERROR);
                    }
                }
            });
        } else {
            // 이미 구매한 아이템이라면 모두 소진시킨다.
            consumeAllItemsForServer();
        }
    }

    // 실제 어플리케이션 서버에 아이템 구매요청을 한다.
    private void consumeItemForServer(final Purchase purchase) {
        final String refinedSKU = mIsTest ? TEST_SKU : purchase.getSku();
        // mItemService는 내부적으로 rest방식의 통신을 어플리케이션 서버와 해주는 통신인스턴스이다.
        // 각자 서버와 통신에 맞게 구현.
        // 통신이 성공하면 실제 소진을 시킨다.
        // 여기서 두번째 인자로 purchase 인스턴스를 준다.
        // 이때에는 mInventory가 갱신이 되어 있지 않는 경우가 종종있다.
        // 따라서 mInventory에서 purchase를 가져오는 것이다니고
        // 해당 구매 콜백으로 부터 가져온다.
        Log.d(TAG, "success... : " + purchase.toString());

        // 소진에 성공하면 하트를 채워줌
        consumeItem(refinedSKU, purchase);
    }

    // 실제 소진 처리.
    private void consumeItem(String sku, Purchase purchase) {
        final String refinedSKU = mIsTest ? TEST_SKU : sku;
        if (purchase != null || mInventory != null && mInventory.hasPurchase(refinedSKU)) {
            // purchase가 있다면 해당 구매 인스턴스를 통해 처리하고, 그게 아니라면 mInventory를 통해처리
            // 보통 구매성공, 소진실패일 경우일때 mInventory.hasPurchase(refinedSKU)를 통해서 가져오면 된다.
            Purchase refiendPurchase = (purchase == null) ? mInventory.getPurchase(refinedSKU) : purchase;

            // 소진처리.
            mHelper.consumeAsync(refiendPurchase, new IabHelper.OnConsumeFinishedListener() {
                @Override
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        // 소진에 성공하면 캐시된 값을 지운다.
                        mOwnedItems.remove(purchase.getSku());

                        APICaller.addHeartCount(mActivity.getToken(), getHeartCount(refinedSKU), purchase.getToken(),
                                new NetworkManager.NetworkListener<SsomResponse<AddHeartCount.Response>>() {
                                    @Override
                                    public void onResponse(SsomResponse<AddHeartCount.Response> response) {
                                        if(response.isSuccess()) {
                                            Log.d(TAG, "success... : " + response.getData().toString());

                                            Intent intent = new Intent();
                                            intent.setAction(MessageManager.BROADCAST_HEART_COUNT_CHANGE);
                                            intent.putExtra(MessageManager.EXTRA_KEY_HEART_COUNT, response.getData().getHeartsCount());
                                            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);

                                            UiUtils.makeToastMessage(mActivity, "구매해주셔서 감사합니다. 하트를 채워드릴게욤 =)");
                                        } else {
                                            mActivity.showErrorMessage();
                                        }
                                        mActivity.dismissProgressDialog();
                                    }
                                });
                    } else {
                        // TODO
//                        ErrorHandler.handleLocalError(mActivity, LocalErrorCode.ITEM_FATAL_ERROR);
                    }
                }
            });
        } else {
            // TODO
//            ErrorHandler.handleLocalError(mActivity, LocalErrorCode.ITEM_FATAL_ERROR);
        }
    }

    private static int sPurchaseCount = 0;
    public void consumeAllItemsForServer() {
        if (mOwnedItems.size() > sPurchaseCount && mOwnedItems.get(sPurchaseCount) != null) {
            final String sku = mOwnedItems.get(sPurchaseCount);
            final Purchase purchase = mInventory.getPurchase(sku);
            if (purchase != null) {
                // 소진한다
                sPurchaseCount++;
                consumeItem(sku, null);
                consumeAllItemsForServer();
            }
            else {
                handleError();
            }
        }
        else {
            handleError();
        }
    }
    private void handleError() {
        sPurchaseCount = 0;
        mOwnedItems.clear();
        if (mActivity != null) {
            mActivity.dismissProgressDialog();
        }
    }

    public void disposeHelper() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    private int getHeartCount(String refinedSKU) {
        int count;
        switch (refinedSKU) {
            case CommonConst.HEART_2:
                count = 2;
                break;
            case CommonConst.HEART_8:
                count = 8;
                break;
            case CommonConst.HEART_17:
                count = 17;
                break;
            case CommonConst.HEART_28:
                count = 28;
                break;
            default:
                count = 0;
                break;
        }
        return count;
    }

    public void flagEndAsync() {
        if(mHelper != null) mHelper.flagEndAsync();
    }

    // 외부 액티비티나 프래그먼트의 onActivityResult함수에서 호출해야 한다.
    // 해당 메소드가 호출되지 않으면 정상적으로 프로세스가 끝나지 않는다.
    // mHelper.handleActivityResult 내부에서 onIabPurchaseFinished메소드가 호출된다.
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            onActivityResultError(requestCode, resultCode, data);
        }
    }

    // 해당 메소드를 상속받아 오버라이딩하여 에러를 처리할 수 있다.
    public void onActivityResultError(int requestCode, int resultCode, Intent data) {
        mActivity.dismissProgressDialog();
    }
}
