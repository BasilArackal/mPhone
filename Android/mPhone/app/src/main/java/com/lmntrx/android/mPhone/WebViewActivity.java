package com.lmntrx.android.mPhone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * GO THROUGH ALL TODOs BEFORE FINALISING APP
 */

public class WebViewActivity extends AppCompatActivity {


    WebView webview;

    ProgressBar progressBar;

    WaveLoadingView waveLoadingView;

    private UrlCache urlCache = null;

    public final String LOG_TAG = "mPhoneStore";


    private final String timerPage="file:///android_asset/timer/timer.html";

    private final String url = timerPage, domain = "shop-mphone-in"; /*"http://shop.mphone.in/"*/

    private final String aboutPage="file:///android_asset/about/about.html";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting Layout
        setContentView(R.layout.activity_webview);
        try {
            //Hiding ActionBar
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage() + " ");
        }

        waveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);

        //To stop orientation change during splash screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        //Declaring View Objects
        ImageView mangoLeft = (ImageView) findViewById(R.id.lmango);
        ImageView mangoRight = (ImageView) findViewById(R.id.rmango);
        final ImageView mangoText = (ImageView) findViewById(R.id.mPhoneText);
        final TextView lmntrxText = (TextView) findViewById(R.id.lmntrxTxt);
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);

        //WebView Code
        //WebView Elements
        webview = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
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
                if (waveLoadingView.getVisibility() == View.VISIBLE)
                    waveLoadingView.setProgressValue(progress);

                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    if (waveLoadingView.getVisibility() == View.VISIBLE)
                        waveLoadingView.setVisibility(View.GONE);
                }
            }
        });

        //setting webViewClient for webView. If not set some webView features won't work
        webview.setWebViewClient(new MyWebViewClient(this));

        //loading url
        if (isNetworkAvailable()) {
            webview.loadUrl(url);
            waveLoadingView.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);

        }
        //Animation code
        mangoLeft.setTranslationX(-2000);
        mangoRight.setTranslationX(2000);
        mangoLeft.animate().translationXBy(2000).setDuration(1500);
        mangoRight.animate().translationXBy(-2000).setDuration(2500);
        new CountDownTimer(3000, 1000) {
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

        splashLayout.setVisibility(View.VISIBLE);
        splashLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().show();
                splashLayout.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }


        }, 6000); // CHANGE THIS VALUE TO ADJUST SPLASH SCREEN DURATION


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_webview, menu);
        return true;
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

    public void goHome(MenuItem item) {
        webview.loadUrl(url);
    }

    public void showInfo(MenuItem item) {
        //TODO Start About Activity or load about page in WebView itself
        webview.loadUrl(aboutPage);
    }

    public void closeApp(MenuItem item) {
        WebViewActivity.this.finish();
    }

    class MyWebViewClient extends WebViewClient {


        public MyWebViewClient(Activity activity) {

            //Caching site
            urlCache = new UrlCache(activity);
            urlCache.register(url, domain + ".html",
                    "text/html", "UTF-8", 6*UrlCache.ONE_HOUR);  //TODO Change ONE_MINUTE to 6*ONE_HOUR later

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (view.getVisibility() == View.GONE) {
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                findViewById(R.id.webView).setVisibility(View.VISIBLE);
            }

            if (isNetworkAvailable()) {

                //Loads clicked link if network is available
                view.loadUrl(url);

            } else {

                //Shows error message when network is not available
                Snackbar.make(view, "Please check your connection", Snackbar.LENGTH_LONG).show();

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

            waveLoadingView.setVisibility(View.GONE);

        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) { //Deprecated : WebResourceRequest instead of String (API level 21)
            return urlCache.load(url);
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