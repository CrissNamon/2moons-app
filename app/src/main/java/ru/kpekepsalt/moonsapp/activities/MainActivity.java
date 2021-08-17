package ru.kpekepsalt.moonsapp.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import im.delight.android.webview.AdvancedWebView;
import ru.kpekepsalt.moonsapp.Application;
import ru.kpekepsalt.moonsapp.R;
import ru.kpekepsalt.moonsapp.webclients.AppChromeClient;
import ru.kpekepsalt.moonsapp.webclients.AppWebViewClient;
import ru.kpekepsalt.moonsapp.webclients.JavaScriptInterface;

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
                new AppWebViewClient(this, webView, splash)
        );
        webView.setWebChromeClient(
                new AppChromeClient(this)
        );
        webView.addJavascriptInterface(
                new JavaScriptInterface(this, this, webView),
                "Android"
        );

        webView.loadUrl(Application.APP_URL);
        application.setupBilling();
    }

    @Override
    protected void onStop() {
        super.onStop();
        application.onStop();
    }
}