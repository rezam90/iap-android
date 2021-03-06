package ir.radical.iap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ir.radical.iap.util.Inventory;
import ir.radical.iap.util.Purchase;
import ir.radical.iap.util.SkuDetails;

/**
 * Created by ali on 12/19/16.
 */

public class PayInventory {
    public Map<String,PaySkuDetails> mSkuMap = new HashMap<String,PaySkuDetails>();
    public Map<String,PayPurchase> mPurchaseMap = new HashMap<String,PayPurchase>();

    PayInventory() { }

    /** Returns the listing details for an in-app product. */
    public PaySkuDetails getSkuDetails(String sku) {
        return mSkuMap.get(sku);
    }

    /** Returns purchase information for a given product, or null if there is no purchase. */
    public PayPurchase getPurchase(String sku) {
        return mPurchaseMap.get(sku);
    }

    /** Returns whether or not there exists a purchase of the given product. */
    public boolean hasPurchase(String sku) {
        return mPurchaseMap.containsKey(sku);
    }

    /** Return whether or not details about the given product are available. */
    public boolean hasDetails(String sku) {
        return mSkuMap.containsKey(sku);
    }

    /**
     * Erase a purchase (locally) from the inventory, given its product ID. This just
     * modifies the Inventory object locally and has no effect on the server! This is
     * useful when you have an existing Inventory object which you know to be up to date,
     * and you have just consumed an item successfully, which means that erasing its
     * purchase data from the Inventory you already have is quicker than querying for
     * a new Inventory.
     */
    public void erasePurchase(String sku) {
        if (mPurchaseMap.containsKey(sku)) mPurchaseMap.remove(sku);
    }

    /** Returns a list of all owned product IDs. */
    List<String> getAllOwnedSkus() {
        return new ArrayList<String>(mPurchaseMap.keySet());
    }

    /** Returns a list of all owned product IDs of a given type */
    List<String> getAllOwnedSkus(String itemType) {
        List<String> result = new ArrayList<String>();
        for (PayPurchase p : mPurchaseMap.values()) {
            if (p.getItemType().equals(itemType)) result.add(p.getSku());
        }
        return result;
    }

    /** Returns a list of all purchases. */
    List<PayPurchase> getAllPurchases() {
        return new ArrayList<PayPurchase>(mPurchaseMap.values());
    }

    void addSkuDetails(PaySkuDetails d) {
        mSkuMap.put(d.getSku(), d);
    }

    void addPurchase(PayPurchase p) {
        mPurchaseMap.put(p.getSku(), p);
    }

    public static PayInventory parse(Inventory inventory){
        PayInventory res = new PayInventory();

        Iterator it = inventory.mSkuMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            res.mSkuMap.put((String) pair.getKey(), PaySkuDetails.parse((SkuDetails) pair.getValue()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        it = inventory.mPurchaseMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            res.mPurchaseMap.put((String) pair.getKey(), PayPurchase.parse((Purchase) pair.getValue()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return res;
    }
}
