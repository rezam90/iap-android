package ir.radical.iap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import ir.radical.iap.Enum.Market;

/**
 * Created by ali on 12/18/16.
 */

public abstract class Pay {
    public Pay(){}
    public Pay(Context context){
        this.mContext = context;
    }

    public static Market getMarket(Context context){
        String permissionBazaar = "com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR";
        String permissionIranApps = "ir.tgbs.iranapps.permission.BILLING";
        String permissionMyket = "ir.mservices.market.BILLING";

        if (context.checkCallingOrSelfPermission(permissionIranApps) == PackageManager.PERMISSION_GRANTED) {
            return Market.IranApps;
        } else if (context.checkCallingOrSelfPermission(permissionMyket) == PackageManager.PERMISSION_GRANTED) {
            return Market.Mykey;
        } else if (context.checkCallingOrSelfPermission(permissionBazaar) == PackageManager.PERMISSION_GRANTED) {
            return Market.CafeBazaar;
        }
        return Market.None;
    }

    public static Pay getPayment(Context context){
        Pay res;
        Market market = getMarket(context);
        if (market == Market.IranApps) {
            res = new IranApps(context);
            res.getPublicKey();
        } else if (market == Market.Mykey) {
            res = new Myket(context);
            res.getPublicKey();
        } else {
            res = new Bazaar(context);
            res.getPublicKey();
        }
        return res;
    }

    public static void setPublicKey(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("key", key).commit();
    }

    public interface OnIabPurchaseFinishedListener{
        void OnIabPurchaseFinished(PayResult result, PayPurchase purchase);
    }
    public interface QueryInventoryFinishedListener {
        void QueryInventoryFinished(PayResult result, PayInventory inventory);
    }
    public interface OnConsumeFinishedListener {
        void onConsumeFinished(PayPurchase purchase, PayResult result);
    }
    public interface OnErrorListener {
        void onMarketNotInstalled();
        void onSetupError();
    }

    public boolean hasSetup;
    public Context mContext;
    public OnIabPurchaseFinishedListener purchaseFinishedListener;
    public QueryInventoryFinishedListener queryInventoryFinishedListener;
    public OnConsumeFinishedListener onConsumeFinishedListener;
    public OnErrorListener onErrorListener;
    public String publicKey;

    public abstract void buy(String sku, int requestCode, Pay.OnIabPurchaseFinishedListener listener, String extraData);
    public abstract void startSetup();
    public abstract void consume(PayPurchase purchase, OnConsumeFinishedListener listener);
    public abstract boolean handleActivityResult(int requestCode, int resultCode, Intent data);
    public abstract void close();
    public abstract void queryInventoryAsync(QueryInventoryFinishedListener listener);

    public abstract String getMarketName();
    public abstract String getMarketTitle();
    public abstract String getMarketPackage();
    public abstract String getPublicKey();

    public void setOnIabPurchaseFinishedListener(OnIabPurchaseFinishedListener listener){
        this.purchaseFinishedListener = listener;
    }

    public void setOnQueryInventoryFinishedListener(QueryInventoryFinishedListener listener){
        this.queryInventoryFinishedListener = listener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener){
        this.onErrorListener = onErrorListener;
    }

    public void setOnConsumeFinishedListener(OnConsumeFinishedListener onConsumeFinishedListener){
        this.onConsumeFinishedListener = onConsumeFinishedListener;
    }
//    void setOnPurchaseFinishListener(IabResult result, Purchase purchase);
//    void setOnQueryInventoryFinishListener(IabResult result, Purchase purchase);
}
