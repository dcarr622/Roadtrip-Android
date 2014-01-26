package com.suchroadtrip.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.suchroadtrip.app.R;

public class LoginToTwitter extends Activity {

    protected static final String CALLBACK_URL_KEY = "CALLBACK_URL_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_twitter);

        Intent intent = getIntent();
        String mUrl = intent.getStringExtra(LoginActivity.AUTHENTICATION_URL_KEY);

        WebView webView = (WebView) findViewById(R.id.webViewLoginToTwitter);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new LoginToTwitterWebViewClient());

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.twitter_webview_title);

        webView.loadUrl(mUrl);
    }


    private class LoginToTwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(getString(R.string.TWITTER_CALLBACK_URL))) {
                Log.d("LoginToTwitter", "shouldOverrideUrlLoading");
                Intent intent;
                intent = new Intent();
                intent.putExtra(CALLBACK_URL_KEY, url);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            return false;
        }
    }

}
