package com.example.chinmay.smsautoreaderdemo;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

public class Webview extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webvieww);
        final ProgressBar bar = (ProgressBar)findViewById(R.id.pro);
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");
        // Initialize SmsAutoReader  class
        SmsAutoReader reader = new SmsAutoReader(Webview.this);
        try {
            reader.StartSmsAutoReader();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebView webView = (WebView) findViewById(R.id.web);
        if (Build.VERSION.SDK_INT >= 19)
        {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else
        {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                Log.e("a",url);
                
                // Finish webview activity on receiving sms code or error in final url
                
                if(url.contains("code=")){
                    Toast.makeText(getApplicationContext(),"Success : "+url.substring(url.indexOf("code=")+5,url.length()),Toast.LENGTH_SHORT).show();
                    finish();
                }
                if(url.contains("error")){
                    Toast.makeText(getApplicationContext(),url.substring(url.indexOf("error"),url.length()),Toast.LENGTH_SHORT).show();
                    finish();
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (view.getProgress() == 100) {
                    bar.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }




}
