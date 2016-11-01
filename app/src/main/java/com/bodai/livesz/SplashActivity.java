package com.bodai.livesz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.bodai.livesz.utils.PrefUtils;

public class SplashActivity extends Activity {
    private RelativeLayout rlroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rlroot = (RelativeLayout) findViewById(R.id.rl_root);

        //旋转动画
        RotateAnimation animRotate = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //动画时间
        animRotate.setDuration(1000);
        //保持动画结束状态
        animRotate.setFillAfter(true);

        //缩放动画
        ScaleAnimation animScale = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animScale.setDuration(1000);
        animScale.setFillAfter(true);

        //渐变动画
        AlphaAnimation animAlpha = new AlphaAnimation(0,1);
        animAlpha.setDuration(2000);
        animAlpha.setFillAfter(true);

        //动画集合
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(animRotate);
        set.addAnimation(animScale);
        set.addAnimation(animAlpha);

        //启动动画
        rlroot.startAnimation(set);

        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束，跳转页面，如果第一次进入，跳新手引导，不然跳主页面
                boolean isFristEnter = PrefUtils.getBoolean(SplashActivity.this,"is_first_enter",true);
                Intent intent;
                if (isFristEnter){
                    //跳新手引导
                    intent = new Intent(getApplicationContext(),GuideActivity.class);
                }else {
                    //主页面
                    intent = new Intent(getApplicationContext(),MainActivity.class);

                }
                startActivity(intent);
                //结束当前页面
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });
    }
}
