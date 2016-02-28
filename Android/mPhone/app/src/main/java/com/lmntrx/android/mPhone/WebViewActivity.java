package com.lmntrx.android.mPhone;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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

    public static Boolean isCached = false;

    private UrlCache urlCache = null;

    public final String LOG_TAG = "mPhoneStoreLogs";


    private final String timerPage = "file:///android_asset/timer/timer.html";

    String URL = null, domain = null;

    private final String aboutPage = "file:///android_asset/about/about.html";

    static Boolean isTimer = false;


    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * Below Feature is disabled temporarily due to incompatibility with some devices
         */
        /*
        //Requesting Feature
        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        */
        super.onCreate(savedInstanceState);


        //Setting Layout
        setContentView(R.layout.activity_webview);

        //Locking Orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
/**
 * Below Feature is disabled temporarily due to incompatibility with some devices
 */
        /*try {
            //Hiding ActionBar
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            } else {
                Log.e(LOG_TAG, "getSupportActionBar() returned null");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getSupportActionBar() returned null");
        }*/


        //Deciding which page URL to load
        String launchDateAndTimeS = "2016-02-28 06:00:00";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date launchDateAndTime = dateFormat.parse(launchDateAndTimeS);

            long currentTime = Calendar.getInstance().getTimeInMillis();
            long launchTime = launchDateAndTime.getTime();

            if (currentTime >= launchTime) {
                //Decided to load shop.mphone.in
                URL = getString(R.string.mPhoneShop_URL);
                domain = getString(R.string.mPhoneShopFileName);
                isTimer = false;
                Log.d(LOG_TAG, "Launched");
            } else {
                //Decided to load timer
                URL = timerPage;
                domain = "timer";
                isTimer = true;
                Log.d(LOG_TAG, "Not Yet Launched");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //WebView Elements
        webview = (ObservableWebView) findViewById(R.id.webView);

        //Caching site
        if (!domain.equals("timer")) {
            urlCache = new UrlCache(WebViewActivity.this);
            urlCache.register(URL, domain + ".html",
                    "text/html", "UTF-8", 6 * UrlCache.ONE_MINUTE);  //TODO Change ONE_MINUTE to 6*ONE_HOUR later
        }


        //Initialising loader
        waveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);


        //Declaring View Objects
        ImageView mangoLeft = (ImageView) findViewById(R.id.lmango);
        ImageView mangoRight = (ImageView) findViewById(R.id.rmango);
        final ImageView mangoText = (ImageView) findViewById(R.id.mPhoneText);
        final TextView lmntrxText = (TextView) findViewById(R.id.lmntrxTxt);
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);

        //WebView Code
/**
 * Below Feature is disabled temporarily due to incompatibility with some devices
 */
       /* //Setting Scroll Listener
        webview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                try {
                    //Hiding And Showing ActionBar onScrollChange
                    if (scrollY > oldScrollY) {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().hide();
                        } else {
                            Log.e(LOG_TAG, "getSupportActionBar() returned null");
                        }
                    } else {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().show();
                        } else {
                            Log.e(LOG_TAG, "getSupportActionBar() returned null");
                        }
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "getSupportActionBar() returned null");
                }
            }
        });*/

        //Changing ProgressBar colour
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        //Only required while loading a URL
        progressBar.setVisibility(View.GONE);

        //Setting ActionBar Title
        setTitle(R.string.app_name);


        //WebView settings
        webview.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //setting webViewClient for webView. If not set some webView features won't work
        webview.setWebViewClient(new MyWebViewClient());

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


        //loading URL.
        if (!isTimer) {
            webview.loadUrl(URL);
            waveLoadingView.setVisibility(View.VISIBLE);
        } else webview.loadUrl(URL);


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
                //getSupportActionBar().show();
                splashLayout.setVisibility(View.GONE);
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
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && !webview.canGoBack())
            confirmClosing();
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
            if (!webview.getUrl().equals(URL))
                webview.loadUrl(URL);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "WebView wasn't initialized due to error in connection");
            if (isNetworkAvailable()) {
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                webview.loadUrl(URL);
                waveLoadingView.setVisibility(View.VISIBLE);
            }
        }

    }

    public void showInfo(MenuItem item) {
        webview.loadUrl(aboutPage);
    }

    public void closeApp(MenuItem item) {
        confirmClosing();
    }

    private void confirmClosing() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        WebViewActivity.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class MyWebViewClient extends WebViewClient {


        public MyWebViewClient() {

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
            } else if (!url.contains("http://shop.mphone.in")) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                WebViewActivity.this.startActivity(i);

                return true;

            } else {
                /**
                 * Below Feature is disabled temporarily
                 */
                /*try {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }else {
                        Log.e(LOG_TAG, "getSupportActionBar() returned null");
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "getSupportActionBar() returned null");
                }*/

                if (view.getVisibility() == View.GONE) {
                    findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
                    findViewById(R.id.webView).setVisibility(View.VISIBLE);
                }

                if (isNetworkAvailable()) {

                    //Loads clicked link if network is available
                    view.loadUrl(url);

                    //Initializes progress bar to change when a link is clicked or while navigating to new page
                    progressBar.setVisibility(View.VISIBLE);

                } else {

                    //Shows error message when network is not available
                    Snackbar.make(view, "Please check your connection", Snackbar.LENGTH_LONG).show();

                }


                return true;

            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            //Finished Loading
            System.out.println("on finish");

            //Showing webView   NB: WebView is hidden only on first load
            if (isCached || url.equals(aboutPage) || url.equals(timerPage)) {
                webview.setVisibility(View.VISIBLE);

                //Hiding Error page
                findViewById(R.id.error_msg_layout).setVisibility(View.GONE);
            } else {

                webview.setVisibility(View.GONE);
                //Hiding Error page
                findViewById(R.id.error_msg_layout).setVisibility(View.VISIBLE);
            }

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
            if (!view.getUrl().equals(URL)) {
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