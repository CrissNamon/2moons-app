package ru.kpekepsalt.moonsapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.Purchase;

import im.delight.android.webview.AdvancedWebView;
import ru.kpekepsalt.moonsapp.billings.BillingClientNullException;
import ru.kpekepsalt.moonsapp.webclients.AppChromeClient;
import ru.kpekepsalt.moonsapp.webclients.AppWebViewClient;
import ru.kpekepsalt.moonsapp.Application;
import ru.kpekepsalt.moonsapp.R;

public class MainActivity extends AppCompatActivity{

    private AdvancedWebView webView;
    private View splash;
    private Application application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        splash = findViewById(R.id.splash);

        application = Application.getInstance(this);

        webView.setWebViewClient(
                new AppWebViewClient(webView, splash)
        );
        webView.setWebChromeClient(
                new AppChromeClient(this)
        );

        webView.loadUrl(Application.APP_URL);
        application.setupBilling();
        try {
            application.getmBillingClient().buyProduct(this, "ru.hiddenproject.sso.dm4500",
                    (purchase) -> {

                    });
        } catch (BillingClientNullException e) {
            Log.d("MOONS", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        application.onStop();
    }
}