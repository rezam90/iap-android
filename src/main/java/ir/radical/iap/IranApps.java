package ir.radical.iap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ir.radical.iap.util.IabHelper;
import ir.radical.iap.util.IabResult;
import ir.radical.iap.util.Inventory;
import ir.radical.iap.util.Purchase;


/**
 * Created by Ali on 05/10/2016.
 */
public class IranApps extends Pay {
    // Debug tag, for logging
    // Debug tag, for logging
    static final String TAG = "IranApps";

    // The helper object
    private IabHelper mHelper;

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    String base64EncodedPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDckAGEYsAMQHLdEPX351xIEntARIE6866jK75ShbD3VZHTzh+xdMc+egJXQX9srFBTZTUHcQUElaeJJaGoQBsQ3nwXVkbRduFkuzAYpscGeaX1o7ZwPsD/er0gxT/amxWYLSm7q602Btki/0Ptn26zTA0fZ6UpxpBI1rOGIj3rkQIDAQAB";

    public boolean setupError = false;

    public IranApps(Context context){
        super(context);


        if (!Utils.isPackageExisted(context, getMarketPackage())){
//            Toast.makeText(context, "برای انجام خرید نیاز به نصب کافه بازار است", Toast.LENGTH_LONG).show();
            if (onErrorListener != null){
                onErrorListener.onMarketNotInstalled();
            }
            return;
        }


        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (queryInventoryFinishedListener != null){
                    queryInventoryFinishedListener.QueryInventoryFinished(PayResult.parse(result), PayInventory.parse(inventory));
                }
            }
        };

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (purchaseFinishedListener != null){
                    purchaseFinishedListener.OnIabPurchaseFinished(PayResult.parse(result), PayPurchase.parse(purchase));
                }
            }
        };

    }


    @Override
    public void buy(String sku, int requestCode, final OnIabPurchaseFinishedListener listener, String extraData) {
        mHelper.launchPurchaseFlow((Activity) mContext, sku, requestCode, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                listener.OnIabPurchaseFinished(PayResult.parse(result), PayPurchase.parse(info));
            }
        }, "purchase");
    }

    @Override
    public void startSetup() {
        mHelper = new IabHelper(mContext, base64EncodedPublicKey);
        mHelper.market_package_name = "ir.tgbs.android.iranapp";
        mHelper.market_package_bind = "ir.tgbs.iranapps.billing.InAppBillingService.BIND";

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    setupError = true;
                }
                // Hooray, IAB is fully set up!
//                mHelper.queryInventoryAsync(mGotInventoryListener);
                hasSetup = true;
            }
        });

    }

    @Override
    public void consume(PayPurchase purchase, final OnConsumeFinishedListener listener) {
        mHelper.consumeAsync(purchase.toOriginal(), new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                listener.onConsumeFinished(PayPurchase.parse(purchase), PayResult.parse(result));
            }
        });
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void close() {
        if (mHelper != null) mHelper.dispose();
    }

    @Override
    public void queryInventoryAsync(final QueryInventoryFinishedListener listener) {
        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                listener.QueryInventoryFinished(PayResult.parse(result), PayInventory.parse(inv));
            }
        });
    }

    @Override
    public String getMarketName() {
        return mContext.getString(R.string.iranapps);
    }

    @Override
    public String getMarketTitle() {
        return mContext.getString(R.string.iranapps_title);
    }

    @Override
    public String getMarketPackage() {
        return mContext.getString(R.string.iranapps_package);
    }

    @Override
    public String getPublicKey() {
        if (publicKey == null || publicKey.length() == 0){
            Utils.getMetaData(mContext, getMarketName() + "_key");
        }
        return publicKey;
    }
}
