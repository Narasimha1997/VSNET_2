package com.example.narasimha.android_vsnet;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Tour extends AppCompatActivity {
    WebView view_web;
    final String VIEW_ULR="https://ai.google/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        view_web=(WebView)findViewById(R.id.google_ai);
        view_web.loadUrl(VIEW_ULR);
        view_web.setWebViewClient(cl);
        view_web.canGoBack(); view_web.canGoForward();
        view_web.getSettings().setJavaScriptEnabled(true);
    }
    WebViewClient cl=new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Uri.parse(url).getHost().equals(VIEW_ULR))
                return false;
            Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
            startActivity(i);
            return true;
        }
    };
}
