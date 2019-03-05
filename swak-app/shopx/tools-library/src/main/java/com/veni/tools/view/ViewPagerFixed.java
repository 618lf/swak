package com.veni.tools.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 *      修复viewpager
 *     v4 的ViewPager点击会崩溃
 */
public class ViewPagerFixed extends android.support.v4.view.ViewPager {
    private boolean isCanScroll = true;
    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 设置其是否能滑动换页
     * @param isCanScroll false 不能换页， true 可以滑动换页
     */
    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return isCanScroll && super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return isCanScroll && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
