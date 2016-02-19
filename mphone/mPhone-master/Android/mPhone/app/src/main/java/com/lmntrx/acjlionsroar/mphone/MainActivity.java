package com.lmntrx.acjlionsroar.mphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
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
        animation2.setStartOffset(00);

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
    }
}
