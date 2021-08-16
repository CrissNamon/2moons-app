package ru.kpekepsalt.moonsapp.billings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import ru.kpekepsalt.moonsapp.lambdas.VoidLambda;

public class UpdatedListener implements PurchasesUpdatedListener {

    private VoidLambda onComplete;

    public UpdatedListener(VoidLambda onComplete)
    {
        this.onComplete = onComplete;
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            onComplete.action();
        }
    }

    public void setOnComplete(VoidLambda onComplete) {
        this.onComplete = onComplete;
    }
}
