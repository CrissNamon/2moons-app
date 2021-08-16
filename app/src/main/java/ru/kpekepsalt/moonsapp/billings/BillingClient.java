package ru.kpekepsalt.moonsapp.billings;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.kpekepsalt.moonsapp.lambdas.ParamLambda;

public class BillingClient implements PurchasesUpdatedListener, BillingClientStateListener {

    private com.android.billingclient.api.BillingClient billingClient;
    private final Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private ParamLambda<Purchase> onComplete;

    private final Context context;

    public BillingClient(Context c, ParamLambda<Purchase> onCompleteDefault){
        context = c;
        onComplete = onCompleteDefault;
        setupBilling();
    }

    private void setupBilling()
    {
        if(billingClient == null) {
            billingClient = com.android.billingclient.api.BillingClient.newBuilder(context)
                    .setListener(
                         this
                    )
                    .build();
            billingClient.startConnection(
                    this
            );
        }
    }

    public void buyProduct(Activity activity, String mSkuId, ParamLambda<Purchase> onComplete)
    {
        setOnComplete(onComplete);
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(
                    getSkuDetails(mSkuId)
                )
                .build();
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK && list != null) {
            for(Purchase purchase : list)
            {
                onComplete.action(purchase);
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {

    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
            querySkuDetails();
            queryPurchases();
        }
    }

    public SkuDetails getSkuDetails(String mSkuId)
    {
        return mSkuDetailsMap.get(mSkuId);
    }

    public void setOnComplete(ParamLambda<Purchase> onComplete) {
        this.onComplete = onComplete;
    }

    private void querySkuDetails()
    {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();

        skuDetailsParamsBuilder.setSkusList(skuList).setType(com.android.billingclient.api.BillingClient.SkuType.INAPP);
        this.billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                }
            }
        });
    }

    private void queryPurchases() {
        billingClient.queryPurchasesAsync(com.android.billingclient.api.BillingClient.SkuType.INAPP, (billingResult, list) -> {
            for(Purchase purchase : list)
            {
                onComplete.action(purchase);
            }
        });
    }

    public Map<String, SkuDetails> getmSkuDetailsMap() {
        return mSkuDetailsMap;
    }

}
