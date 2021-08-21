package ru.hiddenproject.humankindlegacies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import im.delight.android.webview.AdvancedWebView;
import ru.hiddenproject.humankindlegacies.Application;
import ru.hiddenproject.humankindlegacies.R;
import ru.hiddenproject.humankindlegacies.webclients.AppChromeClient;
import ru.hiddenproject.humankindlegacies.webclients.AppWebViewClient;
import ru.hiddenproject.humankindlegacies.webclients.JavaScriptInterface;

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

        application.setupBilling();

        Intent intent = getIntent();
        boolean isFromNotification = intent.getBooleanExtra("notification", false);
        if(isFromNotification){
            webView.loadUrl(Application.APP_MESSAGES_URL);
        }else {
            webView.loadUrl(Application.APP_URL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        application.onStop();
    }
}