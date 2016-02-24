package com.lmntrx.android.mPhone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WebViewActivity extends AppCompatActivity {






    WebView webview;

    ProgressBar progressBar;

    private final String url = "http://brittosaji19.github.io/", domain = "brittosaji19-github-io";  //Replace with "http://shop.mphone.in/" later

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        getSupportActionBar().hide();


        //Animation code
        ImageView mangoLeft=(ImageView)findViewById(R.id.lmango);
        ImageView mangoRight=(ImageView)findViewById(R.id.rmango);
        final ImageView mangoText=(ImageView)findViewById(R.id.mPhoneText);
        mangoLeft.setTranslationX(-2000);
        mangoRight.setTranslationX(2000);
        mangoLeft.animate().translationXBy(2000).setDuration(1500);
        mangoRight.animate().translationXBy(-2000).setDuration(2500);
        CountDownTimer animationCounter=new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mangoText.animate().alpha(1f).setDuration(600);
            }
        }.start();
        //---------------------------------------------
        // SHOWS SPLASH SCREEN AND HIDES IT AFTER GIVEN TIME
        final RelativeLayout splashLayout=(RelativeLayout) findViewById(R.id.splashLayout);

        splashLayout.setVisibility(View.VISIBLE);
        splashLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().show();
                splashLayout.setVisibility(View.GONE);
            }


        }, 6000); // CHANGE THIS VALUE TO ADJUST SPLASH SCREEN DURATION


        //---------------------------------------------

        webview = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        setTitle(R.string.app_name);


        webview.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                progressBar.setProgress(progress);

                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webview.setWebViewClient(new MyWebViewClient(this));

        webview.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            if (webview.getVisibility() == View.GONE) {
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                findViewById(R.id.webView).setVisibility(View.VISIBLE);
            }
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.clearCache(false);
    }

    class MyWebViewClient extends WebViewClient {

        private UrlCache urlCache = null;

        public MyWebViewClient(Activity activity) {
            this.urlCache = new UrlCache(activity);

            this.urlCache.register(url, domain + ".html",
                    "text/html", "UTF-8", UrlCache.ONE_MINUTE);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (view.getVisibility() == View.GONE) {
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                findViewById(R.id.webView).setVisibility(View.VISIBLE);
            }

            if (isNetworkAvailable())
                view.loadUrl(url);
            else {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
               // findViewById(R.id.loadingTXT).setVisibility(View.INVISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.VISIBLE);

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            System.out.println("on finish");
            webview.setVisibility(View.VISIBLE);
            //findViewById(R.id.loadingTXT).setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            return this.urlCache.load(url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            if (!view.getUrl().equals(url)) {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }

            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

            if (!view.getUrl().equals(url)) {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }

            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            if (!view.getUrl().equals(url)) {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }


            super.onReceivedSslError(view, handler, error);
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}