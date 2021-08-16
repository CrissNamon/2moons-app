package ru.kpekepsalt.moonsapp.webclients;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class AppWebViewClient extends WebViewClient {

    private final WebView webView;
    private final View splash;

    public AppWebViewClient(WebView webView, View splash)
    {
        this.webView = webView;
        this.splash = splash;

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
    }


}
