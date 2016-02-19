package com.lmntrx.mPhone;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView launchDate=(TextView)findViewById(R.id.Date);
        launchDate.animate().alpha(1f).setDuration(3000);
        final Animation animation1;
        final Animation animation2;
        animation2= new AlphaAnimation(1.0f, 0.05f);
        animation1 = new AlphaAnimation(0.05f, 1.0f);
        animation1.setDuration(500);
        animation1.setStartOffset(0);

        //animation1 AnimationListener
        animation1.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation2 when animation1 ends (continue)
                launchDate.startAnimation(animation2);
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

        animation2.setDuration(500);
        animation2.setStartOffset(0);

        //animation2 AnimationListener
        animation2.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation1 when animation2 ends (repeat)
                launchDate.startAnimation(animation1);
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

        launchDate.startAnimation(animation1);
        ImageView lMango=(ImageView)findViewById(R.id.leftMango);
        ImageView rMango=(ImageView)findViewById(R.id.rightMango);
        final ImageView text=(ImageView)findViewById(R.id.mPhoneText);
        final RelativeLayout comingSoonLayout=(RelativeLayout)findViewById(R.id.comingSoonLayout);
        final RelativeLayout launcherLayout=(RelativeLayout)findViewById(R.id.launcherLayout);
        lMango.setTranslationX(-2000f);
        rMango.setTranslationX(2000f);
        //  text.setTranslationY(-6000f);
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
            }
        }.start();
    }
}

