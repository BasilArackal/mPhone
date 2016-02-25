package com.lmntrx.android.mPhone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {


    WebView webview;

    ProgressBar progressBar;

    public final String LOG_TAG="mPhoneStore";

    private final String url = "http://www.lmntrx.com/", domain = "lmntrx-com";  //Replace with "http://shop.mphone.in/" later

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting Layout
        setContentView(R.layout.activity_webview);
        try {
            //Hiding ActionBar
            getSupportActionBar().hide();
        }catch (NullPointerException e){
            Log.e(LOG_TAG,e.getMessage()+" ");
        }


        //To stop orientation change during splash screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        //Declaring View Objects
        ImageView mangoLeft=(ImageView)findViewById(R.id.lmango);
        ImageView mangoRight=(ImageView)findViewById(R.id.rmango);
        final ImageView mangoText = (ImageView) findViewById(R.id.mPhoneText);
        final TextView lmntrxText=(TextView) findViewById(R.id.lmntrxTxt);

        //Animation code
        mangoLeft.setTranslationX(-2000);
        mangoRight.setTranslationX(2000);
        mangoLeft.animate().translationXBy(2000).setDuration(1500);
        mangoRight.animate().translationXBy(-2000).setDuration(2500);
        new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mangoText.animate().alpha(1f).setDuration(600);
                lmntrxText.animate().alpha(1f).setDuration(600);
            }
        }.start();
        //END OF ANIMATION


        // SHOWS SPLASH SCREEN AND HIDES IT AFTER GIVEN TIME
        final RelativeLayout splashLayout=(RelativeLayout) findViewById(R.id.splashLayout);

        splashLayout.setVisibility(View.VISIBLE);
        splashLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().show();
                splashLayout.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }


        }, 6000); // CHANGE THIS VALUE TO ADJUST SPLASH SCREEN DURATION


        //WebView Code
        //WebView Elements
        webview = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Only required while loading a url
        progressBar.setVisibility(View.GONE);

        //Setting ActionBar Title
        setTitle(R.string.app_name);


        //WebView settings
        webview.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //getting data for progress bar
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

        //setting webViewClient for webView. If not set some webView features won't work
        webview.setWebViewClient(new MyWebViewClient(this));

        //loading url
        webview.loadUrl(url);
    }

    //To navigate within the webView
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Detecting back button press
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

            //Caching site
            this.urlCache = new UrlCache(activity);
            this.urlCache.register(url, domain + ".html",
                    "text/html", "UTF-8", UrlCache.ONE_MINUTE);  //TODO Change ONE_MINUTE to 6*ONE_HOUR later

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (view.getVisibility() == View.GONE) {
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                findViewById(R.id.webView).setVisibility(View.VISIBLE);
            }

            if (isNetworkAvailable()){

                //Loads clicked link if network is available
                view.loadUrl(url);
            }else {

                //Loads error message when network is not available
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);//TODO Change error_msg_layout
                findViewById(R.id.webView).setVisibility(View.GONE);

            }

            //Initializes progress bar to change when a link is clicked or while navigating to new page
            progressBar.setVisibility(View.VISIBLE);

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            //Finished Loading
            System.out.println("on finish");

            //Showing webView   NB: WebView is hidden only on first load
            webview.setVisibility(View.VISIBLE);

            //Hiding progressBar
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) { //Deprecated : WebResourceRequest instead of String (API level 21)
            return this.urlCache.load(url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            //Some Unknown error
            if (!view.getUrl().equals(url)) {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }

            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

            //Server Side errors
            if (!view.getUrl().equals(url)) {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }

            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            //Network Connectivity issue
            if (!view.getUrl().equals(url)) {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.webView).setVisibility(View.GONE);
            }

            super.onReceivedSslError(view, handler, error);
        }


    }

    private boolean isNetworkAvailable() {
        //Returns Network Status
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}