package com.lmntrx.android.mPhone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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


    private final String timerPage = "file:///android_asset/timer/timer.html";

    private final String mPhoneStoreUrl = "http://shop.mphone.in/";

    String url = null, domain = null;

    private final String aboutPage = "file:///android_asset/about/about.html";

    static Boolean isTimer = false;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting Layout
        setContentView(R.layout.activity_webview);

        //Locking Orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            //Hiding ActionBar
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage() + " ");
        }

        //Deciding which page url to load
        String launchDateAndTimeS = "2016-02-26 09:46:00";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date launchDateAndTime = dateFormat.parse(launchDateAndTimeS);

            long currentTime = Calendar.getInstance().getTimeInMillis();
            long launchTime = launchDateAndTime.getTime();

            if (currentTime >= launchTime) {
                //Decided to load shop.mphone.in
                url = mPhoneStoreUrl;
                domain = "shop-mphone-in";
                isTimer = false;
                Log.d(LOG_TAG, "Launched");
            } else {
                //Decided to load timer
                url = timerPage;
                domain = "timer";
                isTimer = true;
                Log.d(LOG_TAG, "Not Yet Launched");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Initialising loader
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

        //loading url. if timer is to be loaded then no need of network connectivity check
        if (!isTimer)
            if (isNetworkAvailable()) {
                webview.loadUrl(url);
                waveLoadingView.setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
                webview.setVisibility(View.GONE);
            }
        else webview.loadUrl(url);
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
        try {
            webview.clearCache(false);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage() + "WebView wasn't initialized");
        }
    }

    public void goHome(MenuItem item) {

        try {
            if (!webview.getUrl().equals(url))
                webview.loadUrl(url);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "WebView wasn't initialized due to error in connection");
            if (isNetworkAvailable()) {
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                webview.loadUrl(url);
                waveLoadingView.setVisibility(View.VISIBLE);
            }
        }

    }

    public void showInfo(MenuItem item) {
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
                    "text/html", "UTF-8", 6 * UrlCache.ONE_HOUR);  //TODO Change ONE_MINUTE to 6*ONE_HOUR later

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("mailto:")) {
                MailTo mt = MailTo.parse(url);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
                i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                i.putExtra(Intent.EXTRA_CC, mt.getCc());
                i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                i.setType("message/rfc822");
                WebViewActivity.this.startActivity(i);
                view.reload();
                return true;
            } else if (url.startsWith("tel:")) {
                Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                WebViewActivity.this.startActivity(tel);
                return true;
            } else if (!url.contains("shop.mphone.in")) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                WebViewActivity.this.startActivity(i);

                return true;

            } else {

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

            //Server Side errors error_codes > 400
            findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.webView).setVisibility(View.GONE);

            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            //Network Connectivity issue
            findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.webView).setVisibility(View.GONE);

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