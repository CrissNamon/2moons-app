package ru.hiddenproject.humankindlegacies.webclients;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.widget.AppCompatButton;

import ru.hiddenproject.humankindlegacies.Application;


public class AppWebViewClient extends WebViewClient {

    private final WebView webView;
    private final View splash;
    private final Context context;
    private final Application application;

    public AppWebViewClient(Context context, WebView webView, View splash)
    {
        this.webView = webView;
        this.splash = splash;
        this.context = context;

        application = Application.getInstance(context);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(this.webView, true);
        this.webView.getSettings().setBuiltInZoomControls(false);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setKeepScreenOn(true);
        this.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        splash.setVisibility(View.VISIBLE);
        this.webView.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        this.webView.setVisibility(View.VISIBLE);
        splash.setVisibility(View.GONE);
        String token = application.getFirebaseToken();
        if(!token.isEmpty() && !application.isFirebaseTokenSaved())
        {
            webView.evaluateJavascript("javascript:saveFirebaseToken('" + token + "');", value -> {
                try {
                    int status = Integer.parseInt(value);
                    if(status == 0)
                    {
                        application.setFirebaseTokenSaved(true);
                    }
                }catch (NumberFormatException e)
                {
                    Log.d("HumankindLegacies", "FIREBASE TOKEN WAS NOT SAVED");
                }
            });
        }else{
            Log.d("HumankindLegacies", "TOKEN EMPTY");
        }
    }


}
