package com.swak.app.core.ui;

import android.view.Gravity;
import android.widget.Toast;

/**
 * 将一个吐司单例化，并且作防止频繁点击的处理。
 */
public abstract class Toasts {

    private static Toast mToast;
    private static long nextTimeMillis;
    private static int yOffset;

    private static Toast init() {
        if (BaseApplication.me() == null) {
            throw new IllegalArgumentException("Context should not be null!!!");
        }
        if (mToast == null) {
            mToast = Toast.makeText(BaseApplication.me(), null, Toast.LENGTH_SHORT);
            yOffset = mToast.getYOffset();
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM, 0, yOffset);
        mToast.setMargin(0, 0);
        return mToast;
    }

    /**
     * 显示内容
     *
     * @param content
     */
    public static void show(String content) {
        show(content, Gravity.BOTTOM, Toast.LENGTH_SHORT);
    }

    /**
     * 可以设置显示的时间长短
     *
     * @param content
     * @param duration
     */
    public static void show(String content, int duration) {
        show(content, Gravity.BOTTOM, duration);
    }

    /**
     * 基础的实现
     *
     * @param content
     * @param gravity
     * @param duration
     */
    public static void show(String content, int gravity, int duration) {
        long current = System.currentTimeMillis();
        if (mToast == null) init();
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.setGravity(gravity, 0, yOffset);
        nextTimeMillis = current + (duration == Toast.LENGTH_LONG ? 3500 : 2000);
        mToast.show();
    }
}
