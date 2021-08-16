package ru.kpekepsalt.moonsapp.webclients;

import android.content.Context;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import ru.kpekepsalt.moonsapp.Application;
import ru.kpekepsalt.moonsapp.R;

public class AppChromeClient extends WebChromeClient {

    private final Application application;
    private final Context context;

    public AppChromeClient(Context c)
    {
        context = c;
        application = Application.getInstance(c);
    }


    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        application.buildAlert(context,
                context.getResources().getString(R.string.alert_title),
                message,
                context.getResources().getString(R.string.alert_button),
                result::confirm
        ).show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }
}
