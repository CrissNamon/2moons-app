package ru.kpekepsalt.moonsapp;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.Nullable;

import com.android.billingclient.api.Purchase;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ru.kpekepsalt.moonsapp.billings.BillingClient;
import ru.kpekepsalt.moonsapp.billings.BillingClientNullException;
import ru.kpekepsalt.moonsapp.lambdas.ConsumerLambda;
import ru.kpekepsalt.moonsapp.lambdas.ParamLambda;
import ru.kpekepsalt.moonsapp.lambdas.VoidLambda;

public class Application {

    private static Application instance;

    private static final String BILLING_REQUEST_TAG = "BILLING_REQUEST";

    public static final String APP_URL = "http://10.0.2.2:8080/game.php?page=overview";
    public static final String APP_BILLING_URL = "http://10.0.2.2:8080/game.php?page=merchant";

    private Context context;

    private BillingClient mBillingClient;

    private RequestQueue requestQueue;

    private int userID = 1;

    private Application(Context c){
        context = c;
        requestQueue = Volley.newRequestQueue(c);
    }

    public static Application getInstance(Context c)
    {
        if(instance == null)
        {
            instance = new Application(c);
        }
        return instance;
    }

    public void onStop()
    {
        requestQueue.cancelAll(BILLING_REQUEST_TAG);
    }

    public AlertDialog buildAlert(Context context, String title, String message, String button, VoidLambda onClick)
    {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, (dialog, which) -> onClick.action())
                .create();
    }

    public void setupBilling(ParamLambda<Purchase> onCompleteDefault) {
        if(mBillingClient == null)
        {
            mBillingClient = new BillingClient(context, onCompleteDefault);
        }
    }

    public BillingClient getmBillingClient() throws BillingClientNullException
    {
        if(mBillingClient == null)
        {
            throw new BillingClientNullException("Billing client was not created");
        }
        return mBillingClient;
    }

    public void PostStringRequest(String url, Map<String, String> params,
                              ParamLambda<String> onResponse, VoidLambda onError)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                onResponse::action, error -> onError.action()){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        stringRequest.setTag(BILLING_REQUEST_TAG);
        requestQueue.add(stringRequest);
    }

    public void setupBilling()
    {
        if(mBillingClient == null)
        {
            Map<String, String> data = new HashMap<>();
            data.put("user", String.valueOf(userID));
            mBillingClient = new BillingClient(context, (purchase)->{
                data.put("token", purchase.getPurchaseToken());
                PostStringRequest(APP_BILLING_URL, data, ConsumerLambda::empty, ConsumerLambda::empty);
            });
        }
    }

}


