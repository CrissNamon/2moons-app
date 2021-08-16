package ru.kpekepsalt.moonsapp.billings;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.kpekepsalt.moonsapp.lambdas.ParamLambda;

public class ClientStateListener implements BillingClientStateListener {

    private BillingClient billingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private List<Purchase> purchasesList;
    private ParamLambda<Map<String, SkuDetails>> onReady;

    public ClientStateListener(BillingClient billingClient, ParamLambda<Map<String, SkuDetails>> onReady)
    {
        this.billingClient = billingClient;
        this.onReady = onReady;
    }

    @Override
    public void onBillingServiceDisconnected() {

    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            querySkuDetails(onReady);
        }
    }

    private void querySkuDetails(ParamLambda<Map<String, SkuDetails>> onReady)
    {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();

        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        this.billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                }
                onReady.action(mSkuDetailsMap);
            }
        });
    }

    private void queryPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, list) -> purchasesList = list);
    }

    public Map<String, SkuDetails> getmSkuDetailsMap() {
        return mSkuDetailsMap;
    }
}
