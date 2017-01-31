package ir.radical.iap;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ir.radical.iap.util.Purchase;

/**
 * Created by ali on 12/19/16.
 */

public class PayPurchase implements Serializable{
    public int id; // custom
    public boolean consumed; // custom
    String mItemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
    String mOrderId;
    String mPackageName;
    String mSku;
    long mPurchaseTime;
    int mPurchaseState;
    String mDeveloperPayload;
    String mToken;
    String mOriginalJson;
    String mSignature;

    public PayPurchase(){}

    public PayPurchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException {
        mItemType = itemType;
        mOriginalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(mOriginalJson);
        mOrderId = o.optString("orderId");
        mPackageName = o.optString("packageName");
        mSku = o.optString("productId");
        mPurchaseTime = o.optLong("purchaseTime");
        mPurchaseState = o.optInt("purchaseState");
        mDeveloperPayload = o.optString("developerPayload");
        mToken = o.optString("token", o.optString("purchaseToken"));
        mSignature = signature;
    }

    public String getItemType() { return mItemType; }
    public String getOrderId() { return mOrderId; }
    public String getPackageName() { return mPackageName; }
    public String getSku() { return mSku; }
    public long getPurchaseTime() { return mPurchaseTime; }
    public int getPurchaseState() { return mPurchaseState; }
    public String getDeveloperPayload() { return mDeveloperPayload; }
    public String getToken() { return mToken; }
    public String getOriginalJson() { return mOriginalJson; }
    public String getSignature() { return mSignature; }

    @Override
    public String toString() { return "PurchaseInfo(type:" + mItemType + "):" + mOriginalJson; }

    public static PayPurchase parse(Purchase purchase){
        if (purchase == null) return null;
        PayPurchase res = null;
        try {
            res = new PayPurchase(purchase.getItemType(), purchase.getOriginalJson(), purchase.getSignature());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Purchase toOriginal(){
        Purchase res = null;
        try {
            res = new Purchase(getItemType(), getOriginalJson(), getSignature());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getCoins(){
        return getSku().split("_")[1];
    }

    public String getData(){
        Gson gson = new Gson();
        String json = gson.toJson(this, PayPurchase.class);
        return json;
    }

    public static PayPurchase parseJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, PayPurchase.class);
    }
}
