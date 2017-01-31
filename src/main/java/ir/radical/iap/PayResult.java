package ir.radical.iap;

import ir.radical.iap.util.IabHelper;
import ir.radical.iap.util.IabResult;

/**
 * Created by ali on 12/19/16.
 */

public class PayResult {
    int mResponse;
    String mMessage;

    public PayResult(int response, String message) {
        mResponse = response;
        if (message == null || message.trim().length() == 0) {
            mMessage = IabHelper.getResponseDesc(response);
        }
        else {
            mMessage = message + " (response: " + IabHelper.getResponseDesc(response) + ")";
        }
    }
    public int getResponse() { return mResponse; }
    public String getMessage() { return mMessage; }
    public boolean isSuccess() { return mResponse == IabHelper.BILLING_RESPONSE_RESULT_OK; }
    public boolean isFailure() { return !isSuccess(); }
    public String toString() { return "IabResult: " + getMessage(); }

    public static PayResult parse(IabResult result){
        if (result == null) return null;
        PayResult res = new PayResult(result.getResponse(), result.getMessage());
        return res;
    }

    public IabResult toOriginal(){
        IabResult res = new IabResult(getResponse(), getMessage());
        return res;
    }
}
