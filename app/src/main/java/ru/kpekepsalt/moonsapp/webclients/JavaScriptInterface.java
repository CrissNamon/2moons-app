package ru.kpekepsalt.moonsapp.webclients;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import ru.kpekepsalt.moonsapp.Application;
import ru.kpekepsalt.moonsapp.billings.BillingClientNullException;

public class JavaScriptInterface {

    private Context context;
    private Application application;
    private Activity activity;
    private WebView webView;

    public JavaScriptInterface(Context context, Activity activity, WebView webView)
    {
        this.context = context;
        this.activity = activity;
        this.webView = webView;
        this.application = Application.getInstance(this.context);
    }

    @JavascriptInterface
    public void buyProduct(String id) throws BillingClientNullException {
        application
                .getBillingClient()
                .buyProduct(activity, id, (purchase) -> {
                    webView.loadUrl(Application.APP_BILLING_URL
                            + "&user=" + application.getUserID()
                            + "&token=" + purchase.getPurchaseToken()
                    );
                });
    }

    @JavascriptInterface
    public void saveUserId(int id)
    {
        application.saveUserID(id);
    }

}
