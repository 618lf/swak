package com.veni.tools.view.itoast;

import android.view.View;

/**
 * 作者：kkan on 2018/11/23 9:40
 * <p>
 * 当前类注释:
 */
public interface IToast {

    IToast setGravity(int gravity, int xOffset, int yOffset);

    IToast setDuration(int durationMillis);

    /**
     * 不能和{@link #setText(String)}一起使用，要么{@link #setView(View)} 要么{@link #setText(String)}
     */
    IToast setView(View view);

    IToast setMargin(float horizontalMargin, float verticalMargin);

    /**
     * 不能和{@link #setView(View)}一起使用，要么{@link #setView(View)} 要么{@link #setText(String)}
     */
    IToast setText(String text);

    void show();

    void cancel();
}
