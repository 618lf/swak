package com.veni.tools.interfaces;

import android.view.View;

import java.util.Calendar;

/**
 * Created by kkan on 2017/7/24.
 * 重复点击的监听器
 */

public abstract class OnNoFastClickListener implements View.OnClickListener {


    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private int minClickDelayTime = 1000;
    private long lastClickTime = 0;

    public OnNoFastClickListener() {

    }

    public OnNoFastClickListener(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
    }


    protected abstract void onNoDoubleClick(View view);

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > minClickDelayTime) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

}
