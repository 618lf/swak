package com.veni.tools.view.itoast;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * 作者：kkan on 2018/11/23 9:57
 * <p>
 * 当前类注释:
 */
public class SystemToast implements IToast {
    private Toast mToast;

    private Context mContext;

    public SystemToast(Context context) {
        mContext = context;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    public IToast setGravity(int gravity, int xOffset, int yOffset) {
        mToast.setGravity(gravity, xOffset, yOffset);
        return this;
    }

    @Override
    public IToast setDuration(int durationMillis) {
        mToast.setDuration(durationMillis);
        return this;
    }


    /**
     * 不能和{@link #setText(String)}一起使用，要么{@link #setView(View)} 要么{@link #setView(View)}
     *
     * @param view 传入view
     * @return 自身对象
     */
    @Override
    public IToast setView(View view) {
        mToast.setView(view);
        return this;
    }

    @Override
    public IToast setMargin(float horizontalMargin, float verticalMargin) {
        mToast.setMargin(horizontalMargin, verticalMargin);
        return this;
    }

    /**
     * 不能和{@link #setView(View)}一起使用，要么{@link #setView(View)} 要么{@link #setView(View)}
     *
     * @param text 传入字符串
     * @return 自身对象
     */
    @Override
    public IToast setText(String text) {
        mToast.setText(text);
        return this;
    }

    @Override
    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }

    @Override
    public void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
