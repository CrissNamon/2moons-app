package ru.kpekepsalt.moonsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.Purchase;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

    public static final String APP_PREFERENCES = "2moons_settings";
    public static final String APP_PREFERENCES_USER_ID = "user_id";

    private Context context;

    private BillingClient billingClient;

    private RequestQueue requestQueue;

    private SharedPreferences sharedPreferences;

    private String firebaseToken;

    private Application(Context c){
        context = c;
        requestQueue = Volley.newRequestQueue(c);
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        firebaseToken = task.getResult();
                    }else{
                        firebaseToken = "";
                    }
                });
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

    public AlertDialog buildConfirm(Context context, String title, String message,
                                    String posButton, String negButton,
                                    VoidLambda onPositive, VoidLambda onNegative)
    {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(posButton, (dialog, which) -> onPositive.action())
                .setNegativeButton(negButton, ((dialog, which) -> onNegative.action()))
                .create();
    }

    public void setupBilling(ParamLambda<Purchase> onCompleteDefault) {
        if(billingClient == null)
        {
            billingClient = new BillingClient(context, onCompleteDefault);
        }
    }

    public BillingClient getBillingClient() throws BillingClientNullException
    {
        if(billingClient == null)
        {
            throw new BillingClientNullException("Billing client was not created");
        }
        return billingClient;
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
        if(billingClient == null)
        {
            Map<String, String> data = new HashMap<>();
            data.put("user", String.valueOf(getUserID()));
            billingClient = new BillingClient(context, (purchase)->{
                data.put("token", purchase.getPurchaseToken());
                PostStringRequest(APP_BILLING_URL, data, ConsumerLambda::empty, ConsumerLambda::empty);
            });
        }
    }

    public int getUserID()
    {
        return sharedPreferences.getInt(APP_PREFERENCES_USER_ID, 0);
    }

    public void saveUserID(int id)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(APP_PREFERENCES_USER_ID, id);
        editor.apply();
    }

    public String getFirebaseToken()
    {
        return firebaseToken;
    }
}


