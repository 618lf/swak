package com.veni.tools.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.veni.tools.R;

import java.util.List;

/**
 * 多行文字上下滚动
 * Created by kkan on 2016/7/20.
 */
public class TextViewVerticalMore extends ViewFlipper {

    private Context mContext;
    private boolean isSetAnimDuration = false;
    private int interval = 5000;
    /**
     * 动画时间
     */
    private int animDuration = 500;

    public TextViewVerticalMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_in);
        if (isSetAnimDuration) animIn.setDuration(animDuration);
        setInAnimation(animIn);
        Animation animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_out);
        if (isSetAnimDuration) animOut.setDuration(animDuration);
        setOutAnimation(animOut);
    }


    /**
     * 设置循环滚动的View数组
     *
     * @param views View数组
     */
    public void setViews(final List<View> views) {
        if (views == null || views.size() == 0) return;
        removeAllViews();
        for ( int i = 0; i < views.size(); i++) {
            addView(views.get(i));
        }
        startFlipping();
    }
}
