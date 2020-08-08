package com.yrj.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.animation.*;
import android.widget.RelativeLayout;
import android.os.Bundle;
import com.yrj.zhbj.utils.PrefUtils;

/**
 * 闪屏页
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_splash);

        RelativeLayout rlRoot = findViewById(R.id.rl_root);

        //旋转动画,从0度到360度，围绕中心点旋转
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);//动画时间
        rotateAnimation.setFillAfter(true);//保持动画状态

        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);

        //动画集合，效果一起实现
        AnimationSet setAnimation = new AnimationSet(false);
        setAnimation.addAnimation(rotateAnimation);
        setAnimation.addAnimation(scaleAnimation);
        setAnimation.addAnimation(alphaAnimation);

        rlRoot.startAnimation(setAnimation);

        //设置监听
        setAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                //检测有没有展示过引导页
                boolean isGuideShow = PrefUtils.getBoolean(getApplicationContext(), "is_guide_show", false);
                if(!isGuideShow){
                    //跳到新手引导页
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                }else{
                    //跳到主页
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}