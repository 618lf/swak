package com.swak.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.swak.app.core.Logger;
import com.swak.app.core.ui.BaseActivity;

public class Launcher extends BaseActivity {

    @Override
    public int bindLayout() {
        return R.layout.activity_launcher;
    }

    @Override
    public void initParams(Bundle parms) {
        Log.d("TAG", "Launcher.initParams");
    }

    @Override
    public void initView(View view) {

        Logger.d("TAG", "Launcher.initView");

        //隐藏标题栏
        mWindowTitle.hiddeTitleBar();

        //添加动画效果
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转界面
                getOperation().forward(MainActivity.class);
                finish();
                //右往左推出效果
                overridePendingTransition(R.anim.anl_push_left_in, R.anim.anl_push_left_out);
                //转动淡出效果1
                // overridePendingTransition(R.anim.anl_scale_rotate_in,R.anim.anl_alpha_out);
                //下往上推出效果
                overridePendingTransition(R.anim.anl_push_bottom_in, R.anim.anl_push_up_out);
            }
        });
        view.setAnimation(animation);
    }
}
