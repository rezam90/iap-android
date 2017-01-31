package ir.radical.iap;

import org.json.JSONException;
import org.json.JSONObject;

import ir.radical.iap.util.IabHelper;
import ir.radical.iap.util.SkuDetails;

/**
 * Created by ali on 12/19/16.
 */

public class PaySkuDetails {
    String mItemType;
    String mSku;
    String mType;
    String mPrice;
    String mTitle;
    String mDescription;
    String mJson;

    public PaySkuDetails(String jsonSkuDetails) throws JSONException {
        this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public PaySkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
        mItemType = itemType;
        mJson = jsonSkuDetails;
        JSONObject o = new JSONObject(mJson);
        mSku = o.optString("productId");
        mType = o.optString("type");
        mPrice = o.optString("price");
        mTitle = o.optString("title");
        mDescription = o.optString("description");
    }

    public String getSku() { return mSku; }
    public String getType() { return mType; }
    public String getPrice() { return mPrice; }
    public String getTitle() { return mTitle; }
    public String getDescription() { return mDescription; }

    @Override
    public String toString() {
        return "SkuDetails:" + mJson;
    }

    public static PaySkuDetails parse(SkuDetails skuDetails){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", skuDetails.getSku());
            jsonObject.put("type", skuDetails.getType());
            jsonObject.put("mPrice", skuDetails.getPrice());
            jsonObject.put("mTitle", skuDetails.getTitle());
            jsonObject.put("mDescription", skuDetails.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PaySkuDetails res = null;
        try {
            res = new PaySkuDetails(skuDetails.getType(), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}
