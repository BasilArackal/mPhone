package com.lmntrx.android.mPhone;

import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static boolean isFinishing=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView launchDate = (TextView) findViewById(R.id.Date);
        final Animation fadeIn;
        final Animation fadeOut;
        fadeOut = new AlphaAnimation(1.0f, 0.05f);
        fadeIn = new AlphaAnimation(0.05f, 1.0f);
        launchDate.animate().alpha(1f).setDuration(3000);
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(0);

        //fadeIn AnimationListener
        fadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                launchDate.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });

        fadeOut.setDuration(500);
        fadeOut.setStartOffset(0);

        //fadeOut AnimationListener
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeIn when fadeOut ends (repeat)
                launchDate.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });

        launchDate.startAnimation(fadeIn);
        ImageView lMango = (ImageView) findViewById(R.id.leftMango);
        ImageView rMango = (ImageView) findViewById(R.id.rightMango);
        final ImageView text = (ImageView) findViewById(R.id.mPhoneText);
        final RelativeLayout comingSoonLayout = (RelativeLayout) findViewById(R.id.comingSoonLayout);
        final RelativeLayout launcherLayout = (RelativeLayout) findViewById(R.id.launcherLayout);
        if (isFinishing){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            lMango.setTranslationX(-2000f);
            rMango.setTranslationX(2000f);
            lMango.animate().translationXBy(2000f).setDuration(1000);
            rMango.animate().translationXBy(-2000f).setDuration(2000);
            new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished <= 3000) {
                        text.animate().alpha(1f).setDuration(1000);
                    }
                }

                @Override
                public void onFinish() {
                    comingSoonLayout.setVisibility(View.VISIBLE);
                    launcherLayout.setVisibility(View.INVISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }.start();
        }else {
            launcherLayout.setVisibility(View.INVISIBLE);
            comingSoonLayout.setVisibility(View.VISIBLE);
        }

        String launchDateAndTimeS = "2016-02-29 07:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date launchDateAndTime = dateFormat.parse(launchDateAndTimeS);
            System.out.println(launchDateAndTime);
            Date currentDate = new Date();
            long diff = launchDateAndTime.getTime() - currentDate.getTime();
            new CountDownTimer(diff, 1000) {
                @Override
                public void onTick(long diff) {

                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    ((TextView) findViewById(R.id.Date_Timer)).setText(days + "d:" + hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60 + "s");
                }

                @Override
                public void onFinish() {

                }
            }.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {

        //if orientation is changed then isFinishing() returns true
        if (!isFinishing()){
           isFinishing=false;
        }

        super.onDestroy();
    }
}

